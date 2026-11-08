package br.com.lojinhadakatia;

import br.com.lojinhadakatia.catalog.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Testa saldo, disponibilidade efetiva e ledger sem alterar pedidos. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class StockIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired ProductRepository products;
    @Autowired ProductStockMovementRepository movements;
    @Autowired BrandRepository brands;

    @BeforeEach
    void reset() {
        movements.deleteAll();
        products.deleteAll();
        brands.deleteAll();
    }

    private long productId() throws Exception {
        String brand = mvc.perform(post("/api/v1/admin/brands").with(user("admin").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Marca de Estoque\"}"))
            .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long brandId = mapper.readTree(brand).get("id").asLong();
        String product = mvc.perform(post("/api/v1/admin/products").with(user("admin").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name":"Produto com estoque","price":19.90,
                     "brandId":%d,"categoryIds":[],"available":true,
                     "published":true,"images":[]}
                    """.formatted(brandId)))
            .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return mapper.readTree(product).get("id").asLong();
    }

    private void movement(long id, String type, int quantity, int expected) throws Exception {
        mvc.perform(post("/api/v1/admin/products/" + id + "/stock/movements")
                .with(user("admin").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"type":"%s","quantity":%d,"reason":"Ajuste auditavel"}
                    """.formatted(type, quantity)))
            .andExpect(status().is(expected));
    }

    @Test
    void enteringAndLeavingStockUpdatesEffectiveAvailability() throws Exception {
        long id = productId();
        mvc.perform(get("/api/v1/products/" + id))
            .andExpect(status().isOk()).andExpect(jsonPath("$.available").value(false));
        movement(id,"ENTRY",5,201);
        mvc.perform(get("/api/v1/products/" + id))
            .andExpect(status().isOk()).andExpect(jsonPath("$.available").value(true));
        mvc.perform(get("/api/v1/products?available=true"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1));
        movement(id,"EXIT",3,201);
        movement(id,"EXIT",4,409);
        mvc.perform(get("/api/v1/admin/products/" + id + "/stock")
                .with(user("admin").roles("ADMIN")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.quantity").value(2));
        mvc.perform(get("/api/v1/admin/products/" + id + "/stock/movements")
                .with(user("admin").roles("ADMIN")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalElements").value(2))
            .andExpect(jsonPath("$.content[0].resultingQuantity").value(2))
            .andExpect(jsonPath("$.content[0].actor").value("admin"));
        movement(id,"EXIT",2,201);
        mvc.perform(get("/api/v1/products/" + id))
            .andExpect(status().isOk()).andExpect(jsonPath("$.available").value(false));
        mvc.perform(get("/api/v1/products?available=false"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void rejectsAnonymousChangesAndInvalidMovements() throws Exception {
        long id = productId();
        mvc.perform(post("/api/v1/admin/products/" + id + "/stock/movements")
                .with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content("{\"type\":\"ENTRY\",\"quantity\":1,\"reason\":\"teste\"}"))
            .andExpect(status().isUnauthorized());
        movement(id, "ENTRY", 0, 409);
        mvc.perform(post("/api/v1/admin/products/" + id + "/stock/movements")
                .with(user("admin").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"type\":\"ENTRY\",\"quantity\":-1,\"reason\":\"teste\"}"))
            .andExpect(status().isBadRequest());
        mvc.perform(get("/api/v1/admin/products/" + id + "/stock"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void adjustmentAllowsZeroAndLedgerPreventsDeletingRecordedProduct() throws Exception {
        long id = productId();
        movement(id, "ENTRY", 2, 201);
        movement(id, "ADJUSTMENT", 0, 201);
        mvc.perform(get("/api/v1/admin/products/" + id + "/stock")
                .with(user("admin").roles("ADMIN")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.quantity").value(0));
        mvc.perform(put("/api/v1/admin/products/" + id)
                .with(user("admin").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name":"Produto com estoque","price":19.90,
                     "brandId":%d,"categoryIds":[],"available":false,
                     "published":false,"images":[]}
                    """.formatted(products.findById(id).orElseThrow().getBrand().getId())))
            .andExpect(status().isOk());
        mvc.perform(delete("/api/v1/admin/products/" + id)
                .with(user("admin").roles("ADMIN")).with(csrf()))
            .andExpect(status().isConflict());
    }
}
