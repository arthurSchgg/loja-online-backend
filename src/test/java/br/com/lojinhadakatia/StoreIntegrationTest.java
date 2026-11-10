package br.com.lojinhadakatia;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("test")
class StoreIntegrationTest {
    @Autowired MockMvc mvc;
    @Test void institutionContactChangesRequireAdmin() throws Exception {
        String payload="{\"displayName\":\"Lojinha da Kátia\",\"slogan\":\"Bem-vindo\",\"whatsapp\":null,\"instagramUrl\":null,\"email\":null}";
        mvc.perform(put("/api/v1/admin/store").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(payload))
            .andExpect(status().isUnauthorized());
        mvc.perform(put("/api/v1/admin/store").with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(payload))
            .andExpect(status().isOk());
        mvc.perform(get("/api/v1/store")).andExpect(status().isOk())
            .andExpect(jsonPath("$.displayName").value("Lojinha da Kátia"));
    }

    @Test void instagramLinksRejectCredentialsPortsFragmentsAndUntrustedHosts() throws Exception {
        var mapper=new com.fasterxml.jackson.databind.ObjectMapper();
        String valid=mapper.writeValueAsString(java.util.Map.of(
            "displayName","Loja de teste", "instagramUrl","https://www.instagram.com/lojinha.demo/"));
        mvc.perform(put("/api/v1/admin/store").with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(valid))
            .andExpect(status().isOk());
        for(String invalid:java.util.List.of(
            "https://user:password@instagram.com/lojinha.demo/",
            "https://www.instagram.com:8443/lojinha.demo/",
            "https://instagram.com/lojinha.demo/#fragment",
            "http://instagram.com/lojinha.demo/",
            "https://instagram.com.evil.example/lojinha.demo/"
        )){
            String payload=mapper.writeValueAsString(java.util.Map.of(
                "displayName","Loja de teste", "instagramUrl",invalid));
            mvc.perform(put("/api/v1/admin/store").with(user("tester").roles("ADMIN")).with(csrf())
                    .contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isConflict());
        }
        mvc.perform(get("/api/v1/store")).andExpect(status().isOk())
            .andExpect(jsonPath("$.instagramUrl").value("https://www.instagram.com/lojinha.demo/"));
    }
}
