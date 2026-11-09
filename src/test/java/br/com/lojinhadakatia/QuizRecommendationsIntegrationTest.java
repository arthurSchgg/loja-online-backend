package br.com.lojinhadakatia;

import br.com.lojinhadakatia.catalog.repository.*;
import br.com.lojinhadakatia.experience.repository.*;
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
class QuizRecommendationsIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired ExperienceRecommendationRepository recommendations;
    @Autowired ExperienceRepository experiences;
    @Autowired ProductPromotionRepository promotions;
    @Autowired ProductStockMovementRepository stock;
    @Autowired ProductRepository products;
    @Autowired CategoryRepository categories;
    @Autowired BrandRepository brands;

    @BeforeEach void reset() {
        recommendations.deleteAll();
        experiences.deleteAll();
        promotions.deleteAll();
        stock.deleteAll();
        products.deleteAll();
        categories.deleteAll();
        brands.deleteAll();
    }

    private JsonNode adminPost(String url, String body) throws Exception {
        String json=mvc.perform(post(url).with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(json);
    }

    private JsonNode quiz() throws Exception {
        return adminPost("/api/v1/admin/experiences", """
          {"title":"Perfil de teste","published":true,
           "questions":[{"title":"Escolha","position":0,"options":[
             {"label":"Floral","position":0,"resultKey":"floral"},
             {"label":"Amadeirado","position":1,"resultKey":"wood"}]}],
           "results":[
             {"key":"floral","title":"Floral"},
             {"key":"wood","title":"Amadeirado"}]}
          """);
    }

    private String answers(JsonNode quiz) {
        return """
          {"answers":[{"questionId":%d,"optionId":%d}]}
          """.formatted(quiz.at("/questions/0/id").asLong(), quiz.at("/questions/0/options/0/id").asLong());
    }

    @Test void resultWithoutProductsStillReturnsProfile() throws Exception {
        var q=quiz();
        mvc.perform(post("/api/v1/experiences/"+q.get("id").asLong()+"/evaluate")
                .contentType(MediaType.APPLICATION_JSON).content(answers(q)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.key").value("floral"))
            .andExpect(jsonPath("$.recommendations").isEmpty());
    }

    @Test void recommendationRequiresAdminAndLimitsToThree() throws Exception {
        var q=quiz();
        long id=q.get("id").asLong();
        String url="/api/v1/admin/experiences/"+id+"/results/floral/recommendations";
        mvc.perform(put(url).with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content("{\"productIds\":[]}"))
            .andExpect(status().isUnauthorized());
        mvc.perform(put(url).with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content("{\"productIds\":[1,2,3,4]}"))
            .andExpect(status().isBadRequest());
        mvc.perform(put(url).with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content("{\"productIds\":[123456]}"))
            .andExpect(status().isNotFound());
        mvc.perform(put(url).with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content("{\"productIds\":[]}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.productIds").isEmpty());
    }

    @Test void onlyInStockPublishedProductsAreReturned() throws Exception {
        var q=quiz();
        long quizId=q.get("id").asLong();
        long brandId=adminPost("/api/v1/admin/brands", "{\"name\":\"Marca teste\"}")
            .get("id").asLong();
        long productId=adminPost("/api/v1/admin/products", """
          {"name":"Vela teste","price":25.00,"brandId":%d,
           "categoryIds":[],"available":true,"published":true,"images":[]}
          """.formatted(brandId)).get("id").asLong();
        String url="/api/v1/admin/experiences/"+quizId+"/results/floral/recommendations";
        mvc.perform(put(url).with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productIds\":["+productId+"]}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.productIds[0]").value(productId));

        String evaluation="/api/v1/experiences/"+quizId+"/evaluate";
        mvc.perform(post(evaluation).contentType(MediaType.APPLICATION_JSON).content(answers(q)))
            .andExpect(status().isOk()).andExpect(jsonPath("$.recommendations").isEmpty());

        adminPost("/api/v1/admin/products/"+productId+"/stock/movements", """
          {"type":"ENTRY","quantity":2,"reason":"Inventário inicial de teste"}
          """);
        mvc.perform(post(evaluation).contentType(MediaType.APPLICATION_JSON).content(answers(q)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.recommendations[0].id").value(productId))
            .andExpect(jsonPath("$.recommendations[0].available").value(true));
    }
}
