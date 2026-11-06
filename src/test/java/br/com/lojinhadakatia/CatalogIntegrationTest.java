package br.com.lojinhadakatia;

import br.com.lojinhadakatia.catalog.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Exercita fluxo real Controller → Service → JPA, incluindo autenticação.
 * Usa H2 para smoke test rápido; integração PostgreSQL/Testcontainers está planejada.
 */
@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("test")
class CatalogIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ProductRepository products;
    @Autowired BrandRepository brands;
    @Autowired CategoryRepository categories;

    @BeforeEach void clean() {
        products.deleteAll();
        categories.deleteAll();
        brands.deleteAll();
    }

    @Test void catalogInitiallyReturnsEmptyPage() throws Exception {
        mvc.perform(get("/api/v1/products"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test void anonymousVisitorCannotManageProducts() throws Exception {
        mvc.perform(post("/api/v1/admin/products").contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isUnauthorized());
    }

    @Test void adminCanPublishProductAndVisitorCanReadIt() throws Exception {
        String brandResponse=mvc.perform(post("/api/v1/admin/brands").with(httpBasic("tester","testpassword123"))
                .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Marca de Teste\"}"))
            .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long brandId=new com.fasterxml.jackson.databind.ObjectMapper().readTree(brandResponse).get("id").asLong();
        String categoryResponse=mvc.perform(post("/api/v1/admin/categories").with(httpBasic("tester","testpassword123"))
                .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Categoria de Teste\"}"))
            .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long categoryId=new com.fasterxml.jackson.databind.ObjectMapper().readTree(categoryResponse).get("id").asLong();
        String payload="{\"name\":\"Produto demonstrativo\",\"description\":\"Descrição pública\",\"price\":19.90,\"brandId\":"+brandId+
                ",\"categoryIds\":["+categoryId+"],\"available\":true,\"published\":true,\"images\":[]}";
        mvc.perform(post("/api/v1/admin/products").with(httpBasic("tester","testpassword123"))
                .contentType(MediaType.APPLICATION_JSON).content(payload))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Produto demonstrativo"));
        mvc.perform(get("/api/v1/products?q=produto&brandId="+brandId+"&categoryId="+categoryId))
            .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1))
            .andExpect(jsonPath("$.content[0].brand.name").value("Marca de Teste"));
    }

    @Test void invalidProductReturnsFieldErrors() throws Exception {
        mvc.perform(post("/api/v1/admin/products").with(httpBasic("tester","testpassword123"))
                .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"\",\"price\":-1}"))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.title").value("Dados inválidos"));
    }
}
