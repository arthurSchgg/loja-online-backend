package br.com.lojinhadakatia;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Homologa a regra de rascunhos: produtos não publicados nunca podem ser
 * recuperados pelo catálogo público, mesmo quando o ID é conhecido.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CatalogReleaseIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    @Test
    void draftCannotBeReadPubliclyButCanBeReadByAdministrator() throws Exception {
        String brandJson = mvc.perform(post("/api/v1/admin/brands")
                .with(httpBasic("tester", "testpassword123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Marca Rascunho Homologacao\"}"))
            .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        JsonNode brand = mapper.readTree(brandJson);
        String draftPayload = """
            {
              "name": "Produto em rascunho",
              "price": 12.90,
              "brandId": %d,
              "categoryIds": [],
              "available": true,
              "published": false,
              "images": []
            }
            """.formatted(brand.get("id").asLong());

        String draftJson = mvc.perform(post("/api/v1/admin/products")
                .with(httpBasic("tester", "testpassword123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(draftPayload))
            .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        long productId = mapper.readTree(draftJson).get("id").asLong();
        mvc.perform(get("/api/v1/products/" + productId)).andExpect(status().isNotFound());
        mvc.perform(get("/api/v1/admin/products/" + productId)
                .with(httpBasic("tester", "testpassword123")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.published").value(false));
    }

    @Test
    void administrativePaginationRejectsInvalidLimit() throws Exception {
        mvc.perform(get("/api/v1/admin/products?size=100")
                .with(httpBasic("tester", "testpassword123")))
            .andExpect(status().isBadRequest());
    }
}
