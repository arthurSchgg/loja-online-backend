package br.com.lojinhadakatia;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "ADMIN_TOTP_SECRET=GEZDGNBVGY3TQOJQGEZDGNBVGY3TQOJQ")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminTotpLoginIntegrationTest {
    @Autowired MockMvc mvc;

    @Test void correctPasswordAloneDoesNotAuthenticateWhenTotpIsEnabled() throws Exception {
        mvc.perform(post("/api/v1/auth/login").with(csrf())
            .param("username", "tester").param("password", "testpassword123"))
            .andExpect(status().isUnauthorized());
    }
}
