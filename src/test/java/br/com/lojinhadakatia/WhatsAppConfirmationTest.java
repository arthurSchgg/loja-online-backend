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

/** Confirmação pública sem criação de pedidos, com preço e disponibilidade vigentes. */
@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("test")
class WhatsAppConfirmationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    private void store() throws Exception {
        mvc.perform(put("/api/v1/admin/store")
                .with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"displayName":"Loja teste","whatsapp":"5547999999999"}
                    """))
            .andExpect(status().isOk());
    }
    private String confirm(String body,int statusCode) throws Exception {
        return mvc.perform(post("/api/v1/whatsapp/confirm")
                .contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().is(statusCode)).andReturn().getResponse().getContentAsString();
    }

    @Test
    void offeringPriceMustMatchCurrentValue() throws Exception {
        store();
        String created=mvc.perform(post("/api/v1/admin/offerings")
                .with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title":"Oficina de teste","type":"WORKSHOP","price":49.90,"published":true}
                    """))
            .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long id=mapper.readTree(created).get("id").asLong();
        confirm("""
            {"type":"OFFERING","id":%d,"expectedPrice":20.00}
            """.formatted(id),409);
        String response=confirm("""
            {"type":"OFFERING","id":%d,"expectedPrice":49.90}
            """.formatted(id),200);
        org.junit.jupiter.api.Assertions.assertTrue(response.contains("https://wa.me/"));
        org.junit.jupiter.api.Assertions.assertTrue(response.contains("Oficina de teste"));
    }

    @Test
    void productNeedsStockAndMatchingPrice() throws Exception {
        store();
        String brand=mvc.perform(post("/api/v1/admin/brands")
                .with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Marca Confirmacao Unica\"}"))
            .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long brandId=mapper.readTree(brand).get("id").asLong();
        String item=mvc.perform(post("/api/v1/admin/products")
                .with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name":"Produto confirmação","price":25.90,"brandId":%d,
                     "categoryIds":[],"available":true,"published":true,"images":[]}
                    """.formatted(brandId)))
            .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long id=mapper.readTree(item).get("id").asLong();
        confirm("""
            {"type":"PRODUCT","id":%d,"expectedPrice":25.90}
            """.formatted(id),409);
        mvc.perform(post("/api/v1/admin/products/"+id+"/stock/movements")
                .with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"type":"ENTRY","quantity":2,"reason":"Inventário inicial"}
                    """))
            .andExpect(status().isCreated());
        confirm("""
            {"type":"PRODUCT","id":%d,"expectedPrice":20.00}
            """.formatted(id),409);
        confirm("""
            {"type":"PRODUCT","id":%d,"expectedPrice":25.90}
            """.formatted(id),200);
    }

    @Test
    void publicPreflightAllowsConfirmationWithoutCredentials() throws Exception {
        mvc.perform(options("/api/v1/whatsapp/confirm")
                .header("Origin", "http://localhost:5500")
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "content-type"))
            .andExpect(status().isOk())
            .andExpect(header().string("Access-Control-Allow-Origin","http://localhost:5500"));
    }
}
