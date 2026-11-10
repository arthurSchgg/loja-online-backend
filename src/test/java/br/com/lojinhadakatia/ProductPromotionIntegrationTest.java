package br.com.lojinhadakatia;

import br.com.lojinhadakatia.catalog.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import org.junit.jupiter.api.*;
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

/** Vigência, não sobreposição e preço efetivo sem alterar a tabela de banners. */
@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("test")
class ProductPromotionIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired ProductRepository products;
    @Autowired ProductStockMovementRepository stockMovements;
    @Autowired ProductPromotionRepository promotions;
    @Autowired BrandRepository brands;

    @BeforeEach void clean(){
        promotions.deleteAll();
        stockMovements.deleteAll();
        products.deleteAll();
        brands.deleteAll();
    }

    private long product() throws Exception {
        var brand=mvc.perform(post("/api/v1/admin/brands")
                .with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Marca Promo\"}"))
            .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long brandId=mapper.readTree(brand).get("id").asLong();
        var p=mvc.perform(post("/api/v1/admin/products")
                .with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                  {"name":"Produto promocional","price":29.90,"available":true,
                   "brandId":%d,"categoryIds":[],"published":true,"images":[{"url":"https://example.com/product-image.jpg","position":0}]}
                  """.formatted(brandId)))
            .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return mapper.readTree(p).get("id").asLong();
    }

    private String postPromotion(long productId,String payload,int statusCode) throws Exception {
        return mvc.perform(post("/api/v1/admin/products/"+productId+"/promotions")
              .with(user("tester").roles("ADMIN")).with(csrf())
              .contentType(MediaType.APPLICATION_JSON).content(payload))
            .andExpect(status().is(statusCode)).andReturn().getResponse().getContentAsString();
    }

    @Test void activePromotionChangesEffectivePriceAndConflictsAreBlocked() throws Exception {
        long id=product();
        mvc.perform(get("/api/v1/products/"+id))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.price").value(29.90))
          .andExpect(jsonPath("$.effectivePrice").value(29.90));
        String created=postPromotion(id,"""
          {"promotionalPrice":19.90,
           "startsAt":"2020-01-01T00:00:00Z",
           "endsAt":"2099-01-01T00:00:00Z"}
          """,201);
        long promoId=mapper.readTree(created).get("id").asLong();
        mvc.perform(get("/api/v1/products/"+id))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.effectivePrice").value(19.90));
        postPromotion(id,"""
          {"promotionalPrice":17.90,
           "startsAt":"2030-01-01T00:00:00Z",
           "endsAt":"2031-01-01T00:00:00Z"}
          """,409);
        mvc.perform(get("/api/v1/admin/products/"+id+"/promotions")
            .with(user("tester").roles("ADMIN")))
          .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(delete("/api/v1/admin/products/"+id+"/promotions/"+promoId)
            .with(user("tester").roles("ADMIN")).with(csrf()))
          .andExpect(status().isNoContent());
        mvc.perform(get("/api/v1/products/"+id))
          .andExpect(status().isOk()).andExpect(jsonPath("$.effectivePrice").value(29.90));
    }

    @Test void rejectsInvalidPricePeriodAndAnonymousCreation() throws Exception {
        long id=product();
        String path="/api/v1/admin/products/"+id+"/promotions";
        mvc.perform(post(path).with(csrf()).contentType(MediaType.APPLICATION_JSON)
          .content("""
            {"promotionalPrice":12.90,"startsAt":"2030-01-01T00:00:00Z",
             "endsAt":"2031-01-01T00:00:00Z"}
            """))
          .andExpect(status().isUnauthorized());
        postPromotion(id,"""
          {"promotionalPrice":33.90,"startsAt":"2030-01-01T00:00:00Z",
           "endsAt":"2031-01-01T00:00:00Z"}
          """,409);
        postPromotion(id,"""
          {"promotionalPrice":17.90,"startsAt":"2031-01-01T00:00:00Z",
           "endsAt":"2030-01-01T00:00:00Z"}
          """,409);
        postPromotion(id,"""
          {"promotionalPrice":-1,"startsAt":"2030-01-01T00:00:00Z",
           "endsAt":"2031-01-01T00:00:00Z"}
          """,400);
    }
}
