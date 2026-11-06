package br.com.lojinhadakatia;

import br.com.lojinhadakatia.catalog.repository.BrandRepository;
import br.com.lojinhadakatia.catalog.repository.CategoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Regressões de exclusão lógica, reativação e validação de payloads. */
@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("test")
class AdminLookupRegressionTest {
    @Autowired MockMvc mvc;
    @Autowired BrandRepository brands;
    @Autowired CategoryRepository categories;

    @BeforeEach
    void reset() {
        brands.deleteAll();
        categories.deleteAll();
    }

    @Test
    void inactivatedBrandCanBeListedAndReactivatedByAdmin() throws Exception {
        String json = mvc.perform(post("/api/v1/admin/brands")
                .with(httpBasic("tester","testpassword123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Marca Temporaria\"}"))
            .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        long id = new ObjectMapper().readTree(json).get("id").asLong();
        mvc.perform(delete("/api/v1/admin/brands/" + id)
                .with(httpBasic("tester","testpassword123")))
            .andExpect(status().isNoContent());
        mvc.perform(get("/api/v1/brands")).andExpect(jsonPath("$").isEmpty());
        mvc.perform(get("/api/v1/admin/brands")
                .with(httpBasic("tester","testpassword123")))
            .andExpect(jsonPath("$[0].name").value("Marca Temporaria"));
        mvc.perform(put("/api/v1/admin/brands/" + id + "/activate")
                .with(httpBasic("tester","testpassword123")))
            .andExpect(status().isNoContent());
        mvc.perform(get("/api/v1/brands"))
            .andExpect(jsonPath("$[0].name").value("Marca Temporaria"));
    }

    @Test
    void malformedQuizAnswerIsRejectedBeforeBusinessLogic() throws Exception {
        mvc.perform(post("/api/v1/experiences/999999/evaluate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"answers\":[{\"questionId\":null,\"optionId\":1}]}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void adminListRequiresAuthentication() throws Exception {
        mvc.perform(get("/api/v1/admin/categories")).andExpect(status().isUnauthorized());
    }
}
