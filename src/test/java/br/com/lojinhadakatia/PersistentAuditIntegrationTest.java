package br.com.lojinhadakatia;

import br.com.lojinhadakatia.security.audit.AdminAuditEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PersistentAuditIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired AdminAuditEventRepository events;

    @BeforeEach void clearEvents() { events.deleteAll(); }

    @Test
    void successfulAdminWriteIsPersistedWithoutRequestPayload() throws Exception {
        mvc.perform(post("/api/v1/admin/brands").with(user("audit-tester").roles("ADMIN"))
                .with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Marca Teste Auditoria\"}"))
            .andExpect(status().isCreated());
        assertThat(events.count()).isEqualTo(1);
        var event = events.findAll().get(0);
        assertThat(event.getActor()).isEqualTo("audit-tester");
        assertThat(event.getMethod()).isEqualTo("POST");
        assertThat(event.getRoutePattern()).isEqualTo("/api/v1/admin/brands");
        assertThat(event.getStatus()).isEqualTo(201);
        assertThat(event.getOccurredAt()).isNotNull();

        mvc.perform(get("/api/v1/admin/audit-events").with(user("audit-tester").roles("ADMIN")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].actor").value("audit-tester"))
            .andExpect(jsonPath("$.content[0].routePattern").value("/api/v1/admin/brands"));
        assertThat(events.count()).isEqualTo(1); // audit reads don't write audit rows
    }

    @Test
    void anonymousCannotReadAuditAndInvalidWritesAreRecorded() throws Exception {
        mvc.perform(get("/api/v1/admin/audit-events")).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/v1/admin/brands").with(user("audit-tester").roles("ADMIN"))
                .with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"\"}"))
            .andExpect(status().isBadRequest());
        assertThat(events.findAll()).hasSize(1);
        assertThat(events.findAll().get(0).getStatus()).isEqualTo(400);
        mvc.perform(get("/api/v1/admin/audit-events?route=/api/v1/admin/brands")
                .with(user("audit-tester").roles("ADMIN")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalElements").value(1));
    }
}
