package br.com.lojinhadakatia;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Does not reuse the default remote IP to avoid affecting unrelated login tests. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LoginThrottleIntegrationTest {
    @Autowired MockMvc mvc;

    @Test
    void blocksAfterFiveBadLoginsAndReturnsRetryAfter() throws Exception {
        for (int i = 0; i < 5; i++) {
            mvc.perform(post("/api/v1/auth/login").with(csrf())
                    .with(request -> { request.setRemoteAddr("192.0.2.123"); return request; })
                    .param("username", "rate-test-unique")
                    .param("password", "incorrect-password"))
                .andExpect(status().isUnauthorized());
        }
        mvc.perform(post("/api/v1/auth/login").with(csrf())
                .with(request -> { request.setRemoteAddr("192.0.2.123"); return request; })
                .param("username", "rate-test-unique")
                .param("password", "incorrect-password"))
            .andExpect(status().isTooManyRequests())
            .andExpect(header().exists("Retry-After"));

        // A different username from the same source should be throttled as well.
        mvc.perform(post("/api/v1/auth/login").with(csrf())
                .with(request -> { request.setRemoteAddr("192.0.2.123"); return request; })
                .param("username", "different-username")
                .param("password", "incorrect-password"))
            .andExpect(status().isTooManyRequests());
    }

    @Test
    void unrelatedSourceCanStillTryAuthentication() throws Exception {
        mvc.perform(post("/api/v1/auth/login").with(csrf())
                .with(request -> { request.setRemoteAddr("192.0.2.124"); return request; })
                .param("username", "unrelated-username")
                .param("password", "incorrect-password"))
            .andExpect(status().isUnauthorized());
    }
}
