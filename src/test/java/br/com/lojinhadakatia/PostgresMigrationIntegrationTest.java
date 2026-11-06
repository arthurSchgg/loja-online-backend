package br.com.lojinhadakatia;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Contrato de persistência exercitado em PostgreSQL real e schema criado por Flyway.
 * Se Docker estiver indisponível, Testcontainers permite ignorar esta classe localmente.
 * No CI Linux com Docker, o teste deve executar.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers(disabledWithoutDocker = true)
class PostgresMigrationIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> database = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void postgres(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", database::getJdbcUrl);
        registry.add("spring.datasource.username", database::getUsername);
        registry.add("spring.datasource.password", database::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.flyway.enabled", () -> "true");
    }

    @Autowired MockMvc mvc;

    @Test
    void migrationsCreateRealTablesAndAdminCanWrite() throws Exception {
        mvc.perform(get("/api/v1/products"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalElements").value(0));

        mvc.perform(post("/api/v1/admin/brands")
                .with(httpBasic("tester", "testpassword123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Marca Integracao\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Marca Integracao"));
    }
}
