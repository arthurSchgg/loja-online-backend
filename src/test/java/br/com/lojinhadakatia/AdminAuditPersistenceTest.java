package br.com.lojinhadakatia;

import br.com.lojinhadakatia.security.AdminAuditEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

/** Persisted administrative audit is restricted and does not include submitted data. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminAuditPersistenceTest {
    @Autowired MockMvc mvc;
    @Autowired AdminAuditEventRepository audits;

    @BeforeEach void reset() { audits.deleteAll(); }

    @Test
    void logsAdministrativeWriteWithOnlySafeMetadata() throws Exception {
        mvc.perform(post("/api/v1/admin/offerings")
                .with(user("auditor").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title":"Oferta para auditoria","description":"texto confidencial de teste",
                     "type":"WORKSHOP","published":false}
                    """))
            .andExpect(status().isCreated());
        var events = audits.findAll();
        assertEquals(1, events.size());
        var event = events.get(0);
        assertEquals("auditor", event.getActor());
        assertEquals("POST", event.getMethod());
        assertEquals("/api/v1/admin/offerings", event.getRoute());
        assertEquals(201, event.getHttpStatus());
        assertNotNull(event.getOccurredAt());
        assertFalse(event.getRoute().contains("confidencial"));
        assertNull(event.getResourceId());

        mvc.perform(get("/api/v1/admin/audit-events").with(user("auditor").roles("ADMIN")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].actor").value("auditor"))
            .andExpect(jsonPath("$.content[0].method").value("POST"))
            .andExpect(jsonPath("$.content[0].httpStatus").value(201));
        assertEquals(1, audits.count());
    }

    @Test
    void auditListRejectsAnonymousAccess() throws Exception {
        mvc.perform(get("/api/v1/admin/audit-events")).andExpect(status().isUnauthorized());
    }
}
