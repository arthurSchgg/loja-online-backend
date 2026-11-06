package br.com.lojinhadakatia;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Valida as diferenças relevantes do perfil staging sem exigir Docker.
 *
 * Usa banco H2 exclusivamente para verificar o contexto Spring em CI; a
 * integração PostgreSQL/Flyway é testada separadamente pelo pipeline.
 */
@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:staging_profile;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.flyway.enabled=false",
    "app.security.admin-username=staging-admin",
    "app.security.admin-password=staging-test-password-12345"
})
@AutoConfigureMockMvc
@ActiveProfiles("staging")
class StagingProfileIntegrationTest {

    @Autowired private MockMvc mvc;
    @Autowired private Environment environment;

    @Test
    void sessionCookieIsSecureInStaging() {
        assertEquals(Boolean.TRUE,
            environment.getProperty("server.servlet.session.cookie.secure", Boolean.class));
        assertEquals("lax", environment.getProperty("server.servlet.session.cookie.same-site"));
    }

    @Test
    void swaggerUiAndApiDocsAreDisabledInStaging() {
        assertEquals(Boolean.FALSE,
            environment.getProperty("springdoc.swagger-ui.enabled", Boolean.class));
        assertEquals(Boolean.FALSE,
            environment.getProperty("springdoc.api-docs.enabled", Boolean.class));
    }

    @Test
    void stagingKeepsHealthPublicButAdminProtected() throws Exception {
        mvc.perform(get("/actuator/health")).andExpect(status().isOk());
        mvc.perform(get("/api/v1/admin/products")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/v1/auth/csrf"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isNotEmpty());
        mvc.perform(post("/api/v1/admin/brands")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"Sem autenticacao\"}"))
            .andExpect(status().isForbidden());
    }
}
