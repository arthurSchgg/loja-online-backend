package br.com.lojinhadakatia;

import br.com.lojinhadakatia.experience.repository.ExperienceRepository;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Verifica o ciclo publicação → consulta → avaliação anônima, incluindo
 * validação de alternativas e restrição de rascunhos.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ExperienceRegressionIntegrationTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ExperienceRepository repository;

    @BeforeEach
    void resetExperiences() {
        repository.deleteAll();
    }

    @Test
    void publishedExperienceCanBeEvaluatedWithoutAuthentication() throws Exception {
        JsonNode experience = createExperience(true);
        long id = experience.get("id").asLong();
        long questionId = experience.at("/questions/0/id").asLong();
        long optionId = experience.at("/questions/0/options/0/id").asLong();

        mvc.perform(get("/api/v1/experiences/" + id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Descubra seu aroma"))
            .andExpect(jsonPath("$.questions[0].options[0].resultKey").doesNotExist());

        String answers = """
            {"answers":[{"questionId":%d,"optionId":%d}]}
            """.formatted(questionId, optionId);

        mvc.perform(post("/api/v1/experiences/" + id + "/evaluate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(answers))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.key").value("floral"))
            .andExpect(jsonPath("$.title").value("Perfil floral"));
    }

    @Test
    void invalidAnswerCannotBeEvaluated() throws Exception {
        JsonNode experience = createExperience(true);
        long id = experience.get("id").asLong();
        long questionId = experience.at("/questions/0/id").asLong();
        mvc.perform(post("/api/v1/experiences/" + id + "/evaluate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"answers":[{"questionId":%d,"optionId":999999}]}
                    """.formatted(questionId)))
            .andExpect(status().isConflict());
    }

    @Test
    void draftExperienceIsHiddenFromPublic() throws Exception {
        JsonNode experience = createExperience(false);
        mvc.perform(get("/api/v1/experiences/" + experience.get("id").asLong()))
            .andExpect(status().isNotFound());
        mvc.perform(get("/api/v1/experiences"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void incompleteExperienceCannotBePublished() throws Exception {
        mvc.perform(post("/api/v1/admin/experiences")
                .with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title":"Incompleto","published":true,"questions":[],"results":[]}
                    """))
            .andExpect(status().isConflict());
    }

    private JsonNode createExperience(boolean published) throws Exception {
        String payload = """
            {
              "title":"Descubra seu aroma",
              "description":"Perguntas demonstrativas",
              "published":%s,
              "questions":[
                {"title":"Qual aroma você prefere?","position":0,
                 "options":[
                    {"label":"Flores","position":0,"resultKey":"floral"},
                    {"label":"Madeira","position":1,"resultKey":"wood"}
                 ]}
              ],
              "results":[
                {"key":"floral","title":"Perfil floral","description":"Notas florais"},
                {"key":"wood","title":"Perfil amadeirado","description":"Notas amadeiradas"}
              ]
            }
            """.formatted(published);
        String json = mvc.perform(post("/api/v1/admin/experiences")
                .with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(json);
    }
}
