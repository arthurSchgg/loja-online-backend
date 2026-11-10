package br.com.lojinhadakatia;

import br.com.lojinhadakatia.catalog.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CatalogEffectivePriceSortIntegrationTest {
 @Autowired MockMvc mvc;
 @Autowired ObjectMapper mapper;
 @Autowired ProductPromotionRepository promotions;
 @Autowired ProductStockMovementRepository movements;
 @Autowired ProductRepository products;
 @Autowired BrandRepository brands;

 @BeforeEach void reset(){
  promotions.deleteAll();
  movements.deleteAll();
  products.deleteAll();
  brands.deleteAll();
 }

 private JsonNode createAdmin(String path,String json) throws Exception {
  String response=mvc.perform(post(path).with(user("admin").roles("ADMIN")).with(csrf())
      .contentType(MediaType.APPLICATION_JSON).content(json))
      .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
  return mapper.readTree(response);
 }
 private long product(String name,int amount,long brandId) throws Exception {
  return createAdmin("/api/v1/admin/products","""
    {"name":"%s","price":%d,"brandId":%d,"categoryIds":[],
     "published":true,"available":true,"images":[{"url":"https://example.com/product-image.jpg","position":0}]}
    """.formatted(name,amount,brandId)).path("id").asLong();
 }
 private void promote(long productId,int amount) throws Exception {
  createAdmin("/api/v1/admin/products/"+productId+"/promotions","""
    {"promotionalPrice":%d,"startsAt":"2020-01-01T00:00:00Z",
     "endsAt":"2099-01-01T00:00:00Z"}
    """.formatted(amount));
 }

 @Test void effectivePriceSortIsGlobalAcrossPagesAndHonorsMaxPrice() throws Exception {
  long brand=createAdmin("/api/v1/admin/brands","{\"name\":\"Preços\"}").path("id").asLong();
  long premium=product("Premium",120,brand);
  long basic=product("Básico",50,brand);
  long seasonal=product("Sazonal",80,brand);
  promote(premium,30);promote(seasonal,25);

  mvc.perform(get("/api/v1/products?sort=price-asc&page=0&size=1"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.content[0].id").value(seasonal))
      .andExpect(jsonPath("$.content[0].effectivePrice").value(25))
      .andExpect(jsonPath("$.totalElements").value(3));
  mvc.perform(get("/api/v1/products?sort=price-asc&page=1&size=1"))
      .andExpect(status().isOk()).andExpect(jsonPath("$.content[0].id").value(premium))
      .andExpect(jsonPath("$.content[0].effectivePrice").value(30));
  mvc.perform(get("/api/v1/products?sort=price-asc&page=2&size=1"))
      .andExpect(status().isOk()).andExpect(jsonPath("$.content[0].id").value(basic));
  mvc.perform(get("/api/v1/products?sort=price-desc&page=0&size=1"))
      .andExpect(status().isOk()).andExpect(jsonPath("$.content[0].id").value(basic));
  mvc.perform(get("/api/v1/products?sort=price-asc&maxPrice=30"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.totalElements").value(2))
      .andExpect(jsonPath("$.content[0].id").value(seasonal))
      .andExpect(jsonPath("$.content[1].id").value(premium));
 }

 @Test void nameSortIsServerSideAndInvalidSortOrPriceAreRejected() throws Exception {
  long brand=createAdmin("/api/v1/admin/brands","{\"name\":\"Marca ordenação\"}").path("id").asLong();
  long z=product("Zeta",20,brand);
  long a=product("Alfa",40,brand);
  mvc.perform(get("/api/v1/products?sort=name&page=0&size=1"))
      .andExpect(status().isOk()).andExpect(jsonPath("$.content[0].id").value(a));
  mvc.perform(get("/api/v1/products?sort=name&page=1&size=1"))
      .andExpect(status().isOk()).andExpect(jsonPath("$.content[0].id").value(z));
  mvc.perform(get("/api/v1/products?sort=other"))
      .andExpect(status().isBadRequest());
  mvc.perform(get("/api/v1/products?maxPrice=-2"))
      .andExpect(status().isBadRequest());
 }
}
