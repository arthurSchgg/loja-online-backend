package br.com.lojinhadakatia;

import br.com.lojinhadakatia.security.AdminAuditRepository;
import br.com.lojinhadakatia.content.repository.ContentSectionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminAuditPersistenceIntegrationTest {
    @Autowired private MockMvc mvc;
    @Autowired private AdminAuditRepository audits;
    @Autowired private ContentSectionRepository sections;

    @BeforeEach
    void reset() {
        sections.deleteAll();
        audits.deleteAll();
    }

    @Test
    void storesSuccessfulWriteWithoutRequestBodyOrSecrets() throws Exception {
        mvc.perform(post("/api/v1/admin/content-sections")
                .with(user("auditor").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"slug":"privado","title":"SEGREDO_DE_TESTE","body":"NUNCA_GRAVAR_CORPO",
                     "position":1,"published":false}
                    """))
            .andExpect(status().isCreated());

        assertThat(audits.count()).isEqualTo(1L);
        var event = audits.findAll().getFirst();
        assertThat(event.getActor()).isEqualTo("auditor");
        assertThat(event.getHttpMethod()).isEqualTo("POST");
        assertThat(event.getRoutePattern()).isEqualTo("/api/v1/admin/content-sections");
        assertThat(event.getResourcePath()).isEqualTo("/api/v1/admin/content-sections");
        assertThat(event.getHttpStatus()).isEqualTo(201);
        assertThat(event.getCreatedAt()).isNotNull();
        assertThat(event.toString()).doesNotContain("SEGREDO_DE_TESTE", "NUNCA_GRAVAR_CORPO");

        mvc.perform(get("/api/v1/admin/audit-events")
                .with(user("auditor").roles("ADMIN")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].actor").value("auditor"))
            .andExpect(jsonPath("$.content[0].httpStatus").value(201));
        assertThat(audits.count()).isEqualTo(1L);
    }

    @Test
    void recordsRejectedBusinessWriteAndDoesNotAuditGet() throws Exception {
        mvc.perform(post("/api/v1/admin/content-sections")
                .with(user("auditor").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"slug":"primeira","title":"Primeira","position":0,"published":false}
                    """))
            .andExpect(status().isCreated());

        mvc.perform(post("/api/v1/admin/content-sections")
                .with(user("auditor").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"slug":"primeira","title":"Duplicada","position":0,"published":false}
                    """))
            .andExpect(status().isConflict());
        assertThat(audits.count()).isEqualTo(2L);
        assertThat(audits.findAll()).anySatisfy(e -> assertThat(e.getHttpStatus()).isEqualTo(409));

        mvc.perform(get("/api/v1/admin/content-sections")
                .with(user("auditor").roles("ADMIN")))
            .andExpect(status().isOk());
        assertThat(audits.count()).isEqualTo(2L);
    }

    @Test
    void deniesAnonymousAuditListing() throws Exception {
        mvc.perform(get("/api/v1/admin/audit-events"))
            .andExpect(status().isUnauthorized());
    }
    @Test
    void filtersAuditByActionActorResourceAndTimeWithoutLeakingData() throws Exception {
        mvc.perform(post("/api/v1/admin/content-sections")
                .with(user("responsavel").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"slug":"auditoria","title":"Auditável","position":1,"published":false}
                    """))
            .andExpect(status().isCreated());

        mvc.perform(get("/api/v1/admin/audit-events")
                .with(user("responsavel").roles("ADMIN"))
                .param("action", "POST")
                .param("actor", "responsavel")
                .param("resource", "/api/v1/admin/content-sections")
                .param("from", "2020-01-01T00:00:00Z")
                .param("to", "2099-01-01T00:00:00Z"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalElements").value(1));

        mvc.perform(get("/api/v1/admin/audit-events")
                .with(user("responsavel").roles("ADMIN"))
                .param("action", "DELETE"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalElements").value(0));

        mvc.perform(get("/api/v1/admin/audit-events")
                .with(user("responsavel").roles("ADMIN"))
                .param("from", "2030-01-01T00:00:00Z")
                .param("to", "2020-01-01T00:00:00Z"))
            .andExpect(status().isBadRequest());
    }

}
