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

    @Test void optionalHoursAddressAndRegionArePublicOnlyWhenConfigured() throws Exception {
        String payload = """
          {"displayName":"Loja teste","openingHours":"Seg-Sex: 9h às 18h",
           "address":"Rua de exemplo, 100","region":"Santa Catarina"}
          """;
        mvc.perform(put("/api/v1/admin/store").with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(payload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.openingHours").value("Seg-Sex: 9h às 18h"));
        mvc.perform(get("/api/v1/store")).andExpect(status().isOk())
            .andExpect(jsonPath("$.address").value("Rua de exemplo, 100"))
            .andExpect(jsonPath("$.region").value("Santa Catarina"));

        // Empty optional fields must not be published as invented or stale contact data.
        String blank = """
          {"displayName":"Loja teste","openingHours":"  ","address":"","region":null}
          """;
        mvc.perform(put("/api/v1/admin/store").with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(blank))
            .andExpect(status().isOk());
        mvc.perform(get("/api/v1/store")).andExpect(status().isOk())
            .andExpect(jsonPath("$.openingHours").isEmpty())
            .andExpect(jsonPath("$.address").isEmpty())
            .andExpect(jsonPath("$.region").isEmpty());
    }

    @Test void rejectsOversizedOptionalInstitutionalDetails() throws Exception {
        var mapper=new com.fasterxml.jackson.databind.ObjectMapper();
        for(var field:java.util.List.of("openingHours","address","region")){
            String tooLong="x".repeat(field.equals("region")?181:501);
            String payload=mapper.writeValueAsString(java.util.Map.of(
                "displayName","Loja teste",field,tooLong));
            mvc.perform(put("/api/v1/admin/store").with(user("tester").roles("ADMIN")).with(csrf())
                    .contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isBadRequest());
        }
    }
}
