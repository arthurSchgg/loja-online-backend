package br.com.lojinhadakatia;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Exercita autenticação REAL: credenciais, sessão HTTP, CSRF e logout.
 * Outras classes testam a autorização isoladamente com usuários simulados.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminSessionAuthenticationTest {

    @Autowired private MockMvc mvc;

    @Test
    void loginRequiresCsrfToken() throws Exception {
        mvc.perform(post("/api/v1/auth/login")
                .param("username", "tester")
                .param("password", "testpassword123"))
            .andExpect(status().isForbidden());
    }

    @Test
    void invalidCredentialsAreRejected() throws Exception {
        mvc.perform(post("/api/v1/auth/login").with(csrf())
                .param("username", "tester")
                .param("password", "wrong-password"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void anonymousSessionCannotReadAdministrativeResources() throws Exception {
        mvc.perform(get("/api/v1/admin/products"))
            .andExpect(status().isUnauthorized());
        mvc.perform(get("/api/v1/auth/me"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void csrfEndpointPublishesHeaderNameAndToken() throws Exception {
        mvc.perform(get("/api/v1/auth/csrf"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.headerName").value("X-CSRF-TOKEN"))
            .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void validLoginCreatesSessionAndLogoutInvalidatesIt() throws Exception {
        MvcResult login = mvc.perform(post("/api/v1/auth/login")
                .with(csrf())
                .param("username", "tester")
                .param("password", "testpassword123"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.authenticated").value(true))
            .andReturn();

        HttpSession current = login.getRequest().getSession(false);
        org.junit.jupiter.api.Assertions.assertNotNull(current);
        MockHttpSession session = (MockHttpSession) current;

        mvc.perform(get("/api/v1/auth/me").session(session))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("tester"));

        mvc.perform(get("/api/v1/admin/products").session(session))
            .andExpect(status().isOk());

        mvc.perform(post("/api/v1/auth/logout").session(session))
            .andExpect(status().isForbidden());

        mvc.perform(post("/api/v1/auth/logout").session(session).with(csrf()))
            .andExpect(status().isNoContent());
    }

    @Test
    void stateChangingAdminEndpointRejectsMissingCsrfEvenWithLogin() throws Exception {
        MvcResult login = mvc.perform(post("/api/v1/auth/login").with(csrf())
                .param("username", "tester")
                .param("password", "testpassword123"))
            .andExpect(status().isOk())
            .andReturn();

        MockHttpSession session = (MockHttpSession) login.getRequest().getSession(false);
        mvc.perform(post("/api/v1/admin/brands")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Nao pode sem csrf\"}"))
            .andExpect(status().isForbidden());

        mvc.perform(post("/api/v1/admin/brands")
                .session(session)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Teste de Sessao\"}"))
            .andExpect(status().isCreated());
    }
}
