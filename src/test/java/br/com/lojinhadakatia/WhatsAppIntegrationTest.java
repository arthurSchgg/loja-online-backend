package br.com.lojinhadakatia;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Integra publicação de oferta, contato oficial e link seguro de atendimento. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WhatsAppIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    @Test
    void publishedOfferUsesConfiguredPhone() throws Exception {
        mvc.perform(put("/api/v1/admin/store").with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"displayName\":\"Lojinha da Kátia\",\"whatsapp\":\"5547999999999\"}"))
            .andExpect(status().isOk());
        String response = mvc.perform(post("/api/v1/admin/offerings").with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Vela demonstrativa\",\"type\":\"CANDLE\",\"published\":true,\"position\":0}"))
            .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long id = mapper.readTree(response).get("id").asLong();
        mvc.perform(get("/api/v1/offerings/"+id+"/whatsapp"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.url",startsWith("https://wa.me/5547999999999?text=")));
    }
}
