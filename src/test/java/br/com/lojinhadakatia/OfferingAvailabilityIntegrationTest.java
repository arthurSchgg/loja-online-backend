package br.com.lojinhadakatia;

import com.fasterxml.jackson.databind.ObjectMapper;
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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OfferingAvailabilityIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    @Test
    void publishedUnavailableOfferingIsVisibleButCannotOpenWhatsApp() throws Exception {
        String body = mvc.perform(post("/api/v1/admin/offerings")
            .with(user("admin").roles("ADMIN")).with(csrf())
            .contentType(MediaType.APPLICATION_JSON).content("""
                {"title":"Oficina indisponível","type":"WORKSHOP","published":true,"available":false}
                """))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.available").value(false))
            .andReturn().getResponse().getContentAsString();
        long id = json.readTree(body).get("id").asLong();
        mvc.perform(get("/api/v1/offerings/" + id)).andExpect(status().isOk())
            .andExpect(jsonPath("$.available").value(false));
        mvc.perform(get("/api/v1/offerings/" + id + "/whatsapp"))
            .andExpect(status().isConflict());
        mvc.perform(post("/api/v1/whatsapp/confirm")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"type\":\"OFFERING\",\"id\":" + id + "}"))
            .andExpect(status().isConflict());

        mvc.perform(put("/api/v1/admin/offerings/" + id)
            .with(user("admin").roles("ADMIN")).with(csrf())
            .contentType(MediaType.APPLICATION_JSON).content("""
                {"title":"Oficina disponível","type":"WORKSHOP","published":true,"available":true}
                """))
            .andExpect(status().isOk()).andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void legacyRequestDefaultsAvailableToTrueAndPreservesAvailabilityOnUpdate() throws Exception {
        String body = mvc.perform(post("/api/v1/admin/offerings")
            .with(user("admin").roles("ADMIN")).with(csrf())
            .contentType(MediaType.APPLICATION_JSON).content("""
                {"title":"Locação legada","type":"RENTAL","published":true}
                """))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.available").value(true))
            .andReturn().getResponse().getContentAsString();
        long id = json.readTree(body).get("id").asLong();
        mvc.perform(put("/api/v1/admin/offerings/" + id)
            .with(user("admin").roles("ADMIN")).with(csrf())
            .contentType(MediaType.APPLICATION_JSON).content("""
                {"title":"Locação atualizada","type":"RENTAL","published":true}
                """))
            .andExpect(status().isOk()).andExpect(jsonPath("$.available").value(true));
    }
}
