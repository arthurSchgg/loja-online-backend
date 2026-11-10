package br.com.lojinhadakatia;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Exercises the category join and promotional subquery on real PostgreSQL SQL. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@EnabledIfEnvironmentVariable(named="RUN_POSTGRES_INTEGRATION",matches="true")
class PostgresCatalogPriceSortIntegrationTest {
 @DynamicPropertySource
 static void postgres(DynamicPropertyRegistry registry){
  registry.add("spring.datasource.url",()->System.getenv("POSTGRES_TEST_URL"));
  registry.add("spring.datasource.username",()->System.getenv("POSTGRES_TEST_USER"));
  registry.add("spring.datasource.password",()->System.getenv("POSTGRES_TEST_PASSWORD"));
  registry.add("spring.datasource.driver-class-name",()->"org.postgresql.Driver");
  registry.add("spring.jpa.hibernate.ddl-auto",()->"validate");
  registry.add("spring.flyway.enabled",()->"true");
 }

 @Autowired MockMvc mvc;
 @Autowired ObjectMapper mapper;

 private long createAdmin(String url,String json) throws Exception {
  String result=mvc.perform(post(url).with(user("admin").roles("ADMIN")).with(csrf())
      .contentType(MediaType.APPLICATION_JSON).content(json))
      .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
  return mapper.readTree(result).path("id").asLong();
 }

 private long product(String name,long brandId,Long categoryId,int price) throws Exception {
  String categories=categoryId==null?"[]":"["+categoryId+"]";
  return createAdmin("/api/v1/admin/products","""
   {"name":"%s","price":%d,"brandId":%d,"categoryIds":%s,
    "available":true,"published":true,"images":[{"url":"https://example.com/product-image.jpg","position":0}]}
   """.formatted(name,price,brandId,categories));
 }

 @Test void categoryAndEffectivePriceOrderWorkTogetherOnPostgreSQL() throws Exception {
  String tag=UUID.randomUUID().toString().substring(0,8);
  long brand=createAdmin("/api/v1/admin/brands","""
    {"name":"Marca ordenação %s"}
    """.formatted(tag));
  long category=createAdmin("/api/v1/admin/categories","""
    {"name":"Categoria ordenação %s"}
    """.formatted(tag));
  long promoted=product("Produto promocional "+tag,brand,category,130);
  long regular=product("Produto regular "+tag,brand,category,40);
  product("Fora categoria "+tag,brand,null,5);
  createAdmin("/api/v1/admin/products/"+promoted+"/promotions","""
    {"promotionalPrice":30,"startsAt":"2020-01-01T00:00:00Z",
     "endsAt":"2099-01-01T00:00:00Z"}
    """);

  mvc.perform(get("/api/v1/products")
       .param("categoryId",String.valueOf(category))
       .param("sort","price-asc").param("maxPrice","45")
       .param("page","0").param("size","1"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.totalElements").value(2))
      .andExpect(jsonPath("$.content[0].id").value(promoted))
      .andExpect(jsonPath("$.content[0].effectivePrice").value(30));
  mvc.perform(get("/api/v1/products")
       .param("categoryId",String.valueOf(category))
       .param("sort","price-asc").param("maxPrice","45")
       .param("page","1").param("size","1"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.totalElements").value(2))
      .andExpect(jsonPath("$.content[0].id").value(regular))
      .andExpect(jsonPath("$.content[0].effectivePrice").value(40));
 }
}
