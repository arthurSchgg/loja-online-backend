package br.com.lojinhadakatia;

import br.com.lojinhadakatia.experience.repository.ExperienceRepository;
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

/** Exercises curated product links, fallback and public eligibility. */
@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("test")
class ExperienceRecommendationsIntegrationTest {
 @Autowired MockMvc mvc;
 @Autowired ObjectMapper json;
 @Autowired ExperienceRepository experiences;
 @Autowired ProductRepository products;
 @Autowired BrandRepository brands;
 @Autowired ProductStockMovementRepository stock;
 @Autowired ProductPromotionRepository promotions;

 @BeforeEach void clean(){
  experiences.deleteAll();
  promotions.deleteAll();
  stock.deleteAll();
  products.deleteAll();
  brands.deleteAll();
 }
 private String adminPost(String path,String body) throws Exception {
  return mvc.perform(post(path).with(user("admin").roles("ADMIN")).with(csrf())
      .contentType(MediaType.APPLICATION_JSON).content(body))
    .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
 }
 private long product(long brand,String name,boolean published) throws Exception {
  String s=adminPost("/api/v1/admin/products","""
   {"name":"%s","price":20.00,"available":true,"published":%s,"brandId":%d,
    "categoryIds":[],"images":[]}
   """.formatted(name,published,brand));
  return json.readTree(s).get("id").asLong();
 }
 private void stock(long id) throws Exception {
  adminPost("/api/v1/admin/products/"+id+"/stock/movements","""
   {"type":"ENTRY","quantity":3,"reason":"Inventário de teste"}
   """);
 }
 private long experience() throws Exception {
  String s=adminPost("/api/v1/admin/experiences","""
   {"title":"Quiz teste","published":true,
    "questions":[{"title":"Gosta de flores?","position":0,"options":[
      {"label":"Sim","position":0,"resultKey":"floral"},
      {"label":"Não","position":1,"resultKey":"other"}]}],
    "results":[{"key":"floral","title":"Floral"},{"key":"other","title":"Outro"}]}
   """);
  return json.readTree(s).get("id").asLong();
 }
 private String update(long id,String body) throws Exception {
  return mvc.perform(put("/api/v1/admin/experiences/"+id+"/results/floral/recommendations")
     .with(user("admin").roles("ADMIN")).with(csrf())
     .contentType(MediaType.APPLICATION_JSON).content(body))
    .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
 }
 private String evaluate(long experienceId) throws Exception {
  String detail=mvc.perform(get("/api/v1/experiences/"+experienceId))
    .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
  long question=json.readTree(detail).at("/questions/0/id").asLong();
  long option=json.readTree(detail).at("/questions/0/options/0/id").asLong();
  return mvc.perform(post("/api/v1/experiences/"+experienceId+"/evaluate")
    .contentType(MediaType.APPLICATION_JSON)
    .content("{\"answers\":[{\"questionId\":"+question+",\"optionId\":"+option+"}]}"))
    .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
 }
 @Test void unavailableCuratedProductFallsBackToSameBrand() throws Exception {
  long brand=json.readTree(adminPost("/api/v1/admin/brands","{\"name\":\"Marca Quiz\"}")).get("id").asLong();
  long unavailable=product(brand,"Produto sem estoque",true);
  long alternative=product(brand,"Produto disponivel",true);
  stock(alternative);
  long e=experience();
  update(e,"{\"productIds\":["+unavailable+"]}");
  mvc.perform(get("/api/v1/admin/experiences/"+e+"/results/floral/recommendations")
   .with(user("admin").roles("ADMIN")))
   .andExpect(status().isOk()).andExpect(jsonPath("$.productIds[0]").value(unavailable));
  var outcome=json.readTree(evaluate(e));
  org.junit.jupiter.api.Assertions.assertEquals("floral",outcome.get("key").asText());
  org.junit.jupiter.api.Assertions.assertEquals(1,outcome.get("recommendations").size());
  org.junit.jupiter.api.Assertions.assertEquals(alternative,outcome.at("/recommendations/0/id").asLong());
 }
 @Test void missingCuratedProductsDoNotHideQuizResult() throws Exception {
  long e=experience();
  mvc.perform(get("/api/v1/admin/experiences/"+e+"/results/floral/recommendations"))
    .andExpect(status().isUnauthorized());
  mvc.perform(put("/api/v1/admin/experiences/"+e+"/results/floral/recommendations")
    .contentType(MediaType.APPLICATION_JSON).content("{\"productIds\":[]}"))
    .andExpect(status().isForbidden());
  var outcome=json.readTree(evaluate(e));
  org.junit.jupiter.api.Assertions.assertEquals("floral",outcome.get("key").asText());
  org.junit.jupiter.api.Assertions.assertEquals(0,outcome.get("recommendations").size());
 }
 @Test void rejectsDuplicateNonExistingAndTooManyCuratedProducts() throws Exception {
  long brand=json.readTree(adminPost("/api/v1/admin/brands","{\"name\":\"Outra Marca Quiz\"}")).get("id").asLong();
  long p=product(brand,"Produto",true);
  long e=experience();
  mvc.perform(put("/api/v1/admin/experiences/"+e+"/results/floral/recommendations")
    .with(user("admin").roles("ADMIN")).with(csrf()).contentType(MediaType.APPLICATION_JSON)
    .content("{\"productIds\":["+p+","+p+"]}"))
    .andExpect(status().isConflict());
  mvc.perform(put("/api/v1/admin/experiences/"+e+"/results/floral/recommendations")
    .with(user("admin").roles("ADMIN")).with(csrf()).contentType(MediaType.APPLICATION_JSON)
    .content("{\"productIds\":[9999999]}"))
    .andExpect(status().isConflict());
  mvc.perform(put("/api/v1/admin/experiences/"+e+"/results/floral/recommendations")
    .with(user("admin").roles("ADMIN")).with(csrf()).contentType(MediaType.APPLICATION_JSON)
    .content("{\"productIds\":[1,2,3,4]}"))
    .andExpect(status().isBadRequest());
 }
}
