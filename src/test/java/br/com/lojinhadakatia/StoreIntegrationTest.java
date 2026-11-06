package br.com.lojinhadakatia;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("test")
class StoreIntegrationTest {
    @Autowired MockMvc mvc;
    @Test void institutionContactChangesRequireAdmin() throws Exception {
        String payload="{\"displayName\":\"Lojinha da Kátia\",\"slogan\":\"Bem-vindo\",\"whatsapp\":null,\"instagramUrl\":null,\"email\":null}";
        mvc.perform(put("/api/v1/admin/store").contentType(MediaType.APPLICATION_JSON).content(payload))
            .andExpect(status().isUnauthorized());
        mvc.perform(put("/api/v1/admin/store").with(httpBasic("tester","testpassword123"))
                .contentType(MediaType.APPLICATION_JSON).content(payload))
            .andExpect(status().isOk());
        mvc.perform(get("/api/v1/store")).andExpect(status().isOk())
            .andExpect(jsonPath("$.displayName").value("Lojinha da Kátia"));
    }
}
