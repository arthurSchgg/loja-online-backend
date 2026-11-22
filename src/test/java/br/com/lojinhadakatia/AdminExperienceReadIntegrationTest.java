package br.com.lojinhadakatia;

import br.com.lojinhadakatia.experience.repository.ExperienceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
class AdminExperienceReadIntegrationTest {
 @Autowired MockMvc mvc;
 @Autowired ObjectMapper mapper;
 @Autowired ExperienceRepository repository;

 @Test void adminReadsCompleteDraftButPublicDoesNotSeeVotingKeys() throws Exception {
  String body="""
   {"title":"Teste de leitura","description":"rascunho","published":true,
    "questions":[{"title":"Aroma?","position":0,"options":[
      {"label":"Floral","position":0,"resultKey":"floral"},
      {"label":"Amadeirado","position":1,"resultKey":"wood"}]}],
    "results":[{"key":"floral","title":"Flores"},{"key":"wood","title":"Madeiras"}]}
   """;
  var response=mvc.perform(post("/api/v1/admin/experiences").with(user("admin").roles("ADMIN"))
      .with(csrf()).contentType(MediaType.APPLICATION_JSON).content(body))
      .andExpect(status().isCreated()).andReturn();
  long id=mapper.readTree(response.getResponse().getContentAsString()).path("id").asLong();

  mvc.perform(get("/api/v1/admin/experiences/"+id))
      .andExpect(status().isUnauthorized());
  mvc.perform(get("/api/v1/admin/experiences/"+id).with(user("client").roles("USER")))
      .andExpect(status().isForbidden());
  mvc.perform(get("/api/v1/admin/experiences/"+id).with(user("admin").roles("ADMIN")))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.published").value(true))
      .andExpect(jsonPath("$.questions[0].options[0].resultKey").value("floral"))
      .andExpect(jsonPath("$.results[0].key").value("floral"))
      .andExpect(jsonPath("$.results[0].productIds").isArray());
  mvc.perform(get("/api/v1/experiences/"+id))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.results").doesNotExist())
      .andExpect(jsonPath("$.questions[0].options[0].resultKey").doesNotExist());
 }
 @Test void adminCanRoundTripEditAndPublicStaysRedacted() throws Exception {
  String first="""
   {"title":"Antes","published":true,
    "questions":[{"title":"Pergunta","position":0,"options":[
      {"label":"A","position":0,"resultKey":"f"},
      {"label":"B","position":1,"resultKey":"f"}]}],
    "results":[{"key":"f","title":"Perfil"}]}
   """;
  long id=mapper.readTree(mvc.perform(post("/api/v1/admin/experiences")
      .with(user("admin").roles("ADMIN")).with(csrf())
      .contentType(MediaType.APPLICATION_JSON).content(first))
      .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString()).path("id").asLong();
  String revised=first.replace("Antes","Depois");
  mvc.perform(put("/api/v1/admin/experiences/"+id)
      .with(user("admin").roles("ADMIN")).with(csrf())
      .contentType(MediaType.APPLICATION_JSON).content(revised))
      .andExpect(status().isOk()).andExpect(jsonPath("$.title").value("Depois"));
  mvc.perform(get("/api/v1/admin/experiences/"+id).with(user("admin").roles("ADMIN")))
      .andExpect(status().isOk()).andExpect(jsonPath("$.title").value("Depois"))
      .andExpect(jsonPath("$.results[0].key").value("f"))
      .andExpect(jsonPath("$.questions[0].options[1].resultKey").value("f"));
 }

 @Test void updatePreservesExistingProductLinksWhenProfileKeyIsUnchanged() throws Exception {
  String quiz="""
   {"title":"Quiz links","published":true,
    "questions":[{"title":"Aroma","position":0,"options":[
      {"label":"Rosa","position":0,"resultKey":"f"},
      {"label":"Jasmim","position":1,"resultKey":"f"}]}],
    "results":[{"key":"f","title":"Floral"}]}
   """;
  String auth="admin";
  long id=mapper.readTree(mvc.perform(post("/api/v1/admin/experiences")
      .with(user(auth).roles("ADMIN")).with(csrf())
      .contentType(MediaType.APPLICATION_JSON).content(quiz))
      .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString()).path("id").asLong();
  long brandId=mapper.readTree(mvc.perform(post("/api/v1/admin/brands")
      .with(user(auth).roles("ADMIN")).with(csrf()).contentType(MediaType.APPLICATION_JSON)
      .content("{\"name\":\"Marca para teste de quiz\"}"))
      .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString()).path("id").asLong();
  String product="{\"name\":\"Item para quiz\",\"price\":35.00,\"brandId\":"+brandId+
      ",\"categoryIds\":[],\"available\":true,\"published\":true,\"images\":[{\"url\":\"https://example.com/product-image.jpg\",\"position\":0}]}";
  long productId=mapper.readTree(mvc.perform(post("/api/v1/admin/products")
      .with(user(auth).roles("ADMIN")).with(csrf()).contentType(MediaType.APPLICATION_JSON)
      .content(product)).andExpect(status().isCreated()).andReturn()
      .getResponse().getContentAsString()).path("id").asLong();
  mvc.perform(put("/api/v1/admin/experiences/"+id+"/results/f/recommendations")
      .with(user(auth).roles("ADMIN")).with(csrf()).contentType(MediaType.APPLICATION_JSON)
      .content("{\"productIds\":["+productId+"]}")).andExpect(status().isOk());
  mvc.perform(put("/api/v1/admin/experiences/"+id)
      .with(user(auth).roles("ADMIN")).with(csrf()).contentType(MediaType.APPLICATION_JSON)
      .content(quiz.replace("Quiz links","Quiz editado"))).andExpect(status().isOk());
  mvc.perform(get("/api/v1/admin/experiences/"+id).with(user(auth).roles("ADMIN")))
      .andExpect(status().isOk()).andExpect(jsonPath("$.results[0].productIds[0]").value(productId))
      .andExpect(jsonPath("$.title").value("Quiz editado"));
 }

}
