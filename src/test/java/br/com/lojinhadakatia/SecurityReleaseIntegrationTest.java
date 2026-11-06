package br.com.lojinhadakatia;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/** Testes de regressão da fronteira entre rotas públicas e administrativas. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityReleaseIntegrationTest {
    @Autowired MockMvc mvc;

    @Test
    void anonymousCannotReadAdministrativeCatalog() throws Exception {
        mvc.perform(get("/api/v1/admin/products")).andExpect(status().isUnauthorized());
    }

    @Test
    void anonymousCannotWriteInstitutionalContent() throws Exception {
        mvc.perform(post("/api/v1/admin/content-sections")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")).andExpect(status().isUnauthorized());
    }

    @Test
    void unsupportedPublicWriteIsDenied() throws Exception {
        mvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")).andExpect(status().isUnauthorized());
    }

    @Test
    void invalidCredentialsCannotAccessAdministration() throws Exception {
        mvc.perform(get("/api/v1/admin/products")
                .with(httpBasic("tester", "invalid-password")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void publicCatalogRemainsAccessible() throws Exception {
        mvc.perform(get("/api/v1/products")).andExpect(status().isOk());
    }

    @Test
    void publicQuizEvaluationRemainsReachableWithoutCredentials() throws Exception {
        mvc.perform(post("/api/v1/experiences/999999/evaluate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"answers\":[{\"questionId\":1,\"optionId\":2}]}"))
            .andExpect(status().isNotFound());
    }

    @Test
    void authenticatedAdminCanReadCatalog() throws Exception {
        mvc.perform(get("/api/v1/admin/products")
                .with(httpBasic("tester", "testpassword123")))
            .andExpect(status().isOk());
    }
}
