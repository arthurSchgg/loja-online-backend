package br.com.lojinhadakatia;

import br.com.lojinhadakatia.catalog.repository.*;
import org.junit.jupiter.api.*;
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

/**
 * Exercita fluxo real Controller → Service → JPA, incluindo autenticação.
 * Usa H2 para smoke test rápido; integração PostgreSQL/Testcontainers está planejada.
 */
@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("test")
class CatalogIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ProductRepository products;
    @Autowired ProductStockMovementRepository stockMovements;
    @Autowired ProductPromotionRepository promotions;
    @Autowired BrandRepository brands;
    @Autowired CategoryRepository categories;

    @BeforeEach void clean() {
        promotions.deleteAll();
        stockMovements.deleteAll();
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
        mvc.perform(post("/api/v1/admin/products").with(csrf()).contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isUnauthorized());
    }

    @Test void adminCanPublishProductAndVisitorCanReadIt() throws Exception {
        String brandResponse=mvc.perform(post("/api/v1/admin/brands").with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Marca de Teste\"}"))
            .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long brandId=new com.fasterxml.jackson.databind.ObjectMapper().readTree(brandResponse).get("id").asLong();
        String categoryResponse=mvc.perform(post("/api/v1/admin/categories").with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Categoria de Teste\"}"))
            .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long categoryId=new com.fasterxml.jackson.databind.ObjectMapper().readTree(categoryResponse).get("id").asLong();
        String payload="{\"name\":\"Produto demonstrativo\",\"description\":\"Descrição pública\",\"price\":19.90,\"brandId\":"+brandId+
                ",\"categoryIds\":["+categoryId+"],\"available\":true,\"published\":true,\"images\":[{\"url\":\"https://example.com/product-image.jpg\",\"position\":0}]}";
        mvc.perform(post("/api/v1/admin/products").with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(payload))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Produto demonstrativo"));
        mvc.perform(get("/api/v1/products?q=produto&brandId="+brandId+"&categoryId="+categoryId))
            .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1))
            .andExpect(jsonPath("$.content[0].brand.name").value("Marca de Teste"));
    }

    @Test void invalidProductReturnsFieldErrors() throws Exception {
        mvc.perform(post("/api/v1/admin/products").with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"\",\"price\":-1}"))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.title").value("Dados inválidos"));
    }

    @Test void publicCatalogRejectsOversizedOrInvalidSearchFilters() throws Exception {
        mvc.perform(get("/api/v1/products").param("q", "a".repeat(181)))
            .andExpect(status().isBadRequest());
        mvc.perform(get("/api/v1/products").param("brandId", "-1"))
            .andExpect(status().isBadRequest());
        mvc.perform(get("/api/v1/products").param("categoryId", "0"))
            .andExpect(status().isBadRequest());
        mvc.perform(get("/api/v1/products").param("maxPrice", "12.345"))
            .andExpect(status().isBadRequest());
        mvc.perform(get("/api/v1/products").param("page", "10001"))
            .andExpect(status().isBadRequest());
        mvc.perform(get("/api/v1/products").param("size", "51"))
            .andExpect(status().isBadRequest());
        mvc.perform(get("/api/v1/products").param("page", "0").param("size", "12")
                .param("q", "floral"))
            .andExpect(status().isOk());
    }
}
