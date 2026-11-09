package br.com.lojinhadakatia;

import com.fasterxml.jackson.databind.ObjectMapper;
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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductArchiveIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    @Test
    void archivedProductKeepsHistoryAndCannotBePublishedWithoutRestoring() throws Exception {
        var brandResponse=mvc.perform(post("/api/v1/admin/brands")
                .with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name":"Marca de arquivamento"}
                    """))
            .andExpect(status().isCreated()).andReturn();
        long brand=mapper.readTree(brandResponse.getResponse().getContentAsString()).get("id").asLong();

        String payload="""
            {"name":"Produto arquivável","price":35.90,"brandId":%d,
             "categoryIds":[],"images":[],"available":true,"published":true}
            """.formatted(brand);
        var created=mvc.perform(post("/api/v1/admin/products")
                .with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(payload))
            .andExpect(status().isCreated()).andReturn();
        long id=mapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        mvc.perform(post("/api/v1/admin/products/" + id + "/stock/movements")
                .with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"type":"ENTRY","quantity":4,"reason":"Estoque inicial"}
                    """))
            .andExpect(status().isCreated());

        mvc.perform(put("/api/v1/admin/products/" + id + "/archive")
                .with(user("tester").roles("ADMIN")).with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.archived").value(true))
            .andExpect(jsonPath("$.published").value(false));

        mvc.perform(get("/api/v1/products/" + id))
            .andExpect(status().isNotFound());
        mvc.perform(get("/api/v1/products/" + id + "/whatsapp"))
            .andExpect(status().isNotFound());

        mvc.perform(get("/api/v1/admin/products/" + id)
                .with(user("tester").roles("ADMIN")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.archived").value(true));

        mvc.perform(put("/api/v1/admin/products/" + id)
                .with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(payload))
            .andExpect(status().isConflict());

        mvc.perform(put("/api/v1/admin/products/" + id + "/restore")
                .with(user("tester").roles("ADMIN")).with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.archived").value(false))
            .andExpect(jsonPath("$.published").value(false));

        mvc.perform(get("/api/v1/products/" + id))
            .andExpect(status().isNotFound());

        mvc.perform(get("/api/v1/admin/products/" + id + "/stock/movements")
                .with(user("tester").roles("ADMIN")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].resultingQuantity").value(4));
    }

    @Test
    void anonymousVisitorCannotArchiveOrRestoreProducts() throws Exception {
        mvc.perform(put("/api/v1/admin/products/123/restore").with(csrf()))
            .andExpect(status().isUnauthorized());
        mvc.perform(put("/api/v1/admin/products/123/archive").with(csrf()))
            .andExpect(status().isUnauthorized());
    }
}
