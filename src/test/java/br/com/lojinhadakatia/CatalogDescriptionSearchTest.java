package br.com.lojinhadakatia;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** As buscas devem considerar descrição e proteger caracteres especiais LIKE. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CatalogDescriptionSearchTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    @Test
    void publicSearchFindsTermOnlyPresentInDescription() throws Exception {
        String brand = mvc.perform(post("/api/v1/admin/brands")
                .with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Marca Descricao Especial\"}"))
            .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long brandId = mapper.readTree(brand).get("id").asLong();
        mvc.perform(post("/api/v1/admin/products")
                .with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name":"Vela exemplo",
                     "description":"Aroma de lavanda unica",
                     "price":24.90,
                     "brandId":%d,
                     "categoryIds":[],
                     "published":true,
                     "available":true,
                     "images":[{"url":"https://example.com/product-image.jpg","position":0}]}
                    """.formatted(brandId)))
            .andExpect(status().isCreated());
        mvc.perform(get("/api/v1/products").param("q", "lavanda unica"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalElements").value(1))
            .andExpect(jsonPath("$.content[0].name").value("Vela exemplo"));
    }
}
