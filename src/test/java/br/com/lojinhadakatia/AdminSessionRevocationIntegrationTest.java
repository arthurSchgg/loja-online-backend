package br.com.lojinhadakatia;

import br.com.lojinhadakatia.security.AdminCredentialService;
import br.com.lojinhadakatia.security.AdminSessionVersionFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * A regression covering genuine form authentication and session persistence:
 * password rotation must revoke ALL previously authenticated sessions.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AdminSessionRevocationIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired AdminCredentialService credentials;

    @Test
    void passwordRotationRevokesBothExistingAdministratorSessions() throws Exception {
        long before = credentials.credentialVersion("tester");
        MockHttpSession first = authenticateFrom("198.51.100.31");
        MockHttpSession second = authenticateFrom("198.51.100.32");

        assertEquals(before, first.getAttribute(AdminSessionVersionFilter.VERSION_ATTRIBUTE));
        assertEquals(before, second.getAttribute(AdminSessionVersionFilter.VERSION_ATTRIBUTE));
        mvc.perform(get("/api/v1/auth/me").session(first))
            .andExpect(status().isOk());
        mvc.perform(get("/api/v1/auth/me").session(second))
            .andExpect(status().isOk());

        credentials.changePassword("tester", "testpassword123",
            "a-different-secure-passphrase-1234", "a-different-secure-passphrase-1234");
        assertEquals(before + 1, credentials.credentialVersion("tester"));

        mvc.perform(get("/api/v1/auth/me").session(first))
            .andExpect(status().isUnauthorized());
        mvc.perform(get("/api/v1/auth/me").session(second))
            .andExpect(status().isUnauthorized());
    }

    private MockHttpSession authenticateFrom(String address) throws Exception {
        MockHttpSession session = new MockHttpSession();
        mvc.perform(post("/api/v1/auth/login").session(session).with(csrf())
                .with(request -> { request.setRemoteAddr(address); return request; })
                .param("username", "tester")
                .param("password", "testpassword123"))
            .andExpect(status().isOk());
        assertNotNull(session.getAttribute(AdminSessionVersionFilter.VERSION_ATTRIBUTE));
        return session;
    }
}
