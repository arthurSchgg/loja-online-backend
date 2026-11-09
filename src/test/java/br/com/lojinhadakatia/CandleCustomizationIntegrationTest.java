package br.com.lojinhadakatia;

import br.com.lojinhadakatia.offering.repository.CandleOptionRepository;
import br.com.lojinhadakatia.offering.repository.ServiceOfferingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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

/** End-to-end API contract for configured candle choices and WhatsApp validation. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CandleCustomizationIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired CandleOptionRepository options;
    @Autowired ServiceOfferingRepository offerings;

    @BeforeEach
    void reset() {
        options.deleteAll();
        offerings.deleteAll();
    }

    private long createOffering(String type, boolean published) throws Exception {
        String body = """
            {"title":"Oferta teste","type":"%s","published":%s}
            """.formatted(type, published);
        String response = mvc.perform(post("/api/v1/admin/offerings")
                .with(user("admin").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return json.readTree(response).get("id").asLong();
    }

    @Test
    void publishedCandleHasConfiguredOptionsAndConfirmedMessage() throws Exception {
        mvc.perform(put("/api/v1/admin/store")
            .with(user("admin").roles("ADMIN")).with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"displayName":"Loja teste","whatsapp":"5547999999999"}
                """))
            .andExpect(status().isOk());

        long id = createOffering("CANDLE", true);
        String adminUrl = "/api/v1/admin/offerings/" + id + "/options";
        mvc.perform(put(adminUrl).with(user("admin").roles("ADMIN")).with(csrf())
            .contentType(MediaType.APPLICATION_JSON).content("""
                [{"kind":"AROMA","value":"Lavanda","active":true},
                 {"kind":"COLOR","value":"Azul","active":true},
                 {"kind":"PACKAGING","value":"Caixa presente","active":false}]
                """))
            .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(3));

        mvc.perform(get("/api/v1/offerings/" + id + "/options"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].kind").exists());

        mvc.perform(post("/api/v1/whatsapp/confirm")
            .contentType(MediaType.APPLICATION_JSON).content("""
                {"type":"OFFERING","id":%d,"options":{"AROMA":"Lavanda","COLOR":"Azul"}}
                """.formatted(id)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Lavanda")))
            .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Azul")))
            .andExpect(jsonPath("$.url").value(org.hamcrest.Matchers.startsWith("https://wa.me/5547999999999")));
    }

    @Test
    void rejectsUnknownInactiveOrDuplicateOptions() throws Exception {
        long id = createOffering("CANDLE", true);
        String url = "/api/v1/admin/offerings/" + id + "/options";
        mvc.perform(put(url).with(user("admin").roles("ADMIN")).with(csrf())
            .contentType(MediaType.APPLICATION_JSON).content("""
                [{"kind":"AROMA","value":"Lavanda","active":true},
                 {"kind":"PACKAGING","value":"Caixa","active":false}]
                """))
            .andExpect(status().isOk());

        for (String selection : new String[] {
            "\"AROMA\":\"Outra\"", "\"PACKAGING\":\"Caixa\""
        }) {
            mvc.perform(post("/api/v1/whatsapp/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"type\":\"OFFERING\",\"id\":" + id + ",\"options\":{" + selection + "}}"))
                .andExpect(status().isConflict());
        }

        mvc.perform(put(url).with(user("admin").roles("ADMIN")).with(csrf())
            .contentType(MediaType.APPLICATION_JSON).content("""
                [{"kind":"AROMA","value":"Lavanda","active":true},
                 {"kind":"AROMA","value":"lavanda","active":true}]
                """))
            .andExpect(status().isConflict());
    }

    @Test
    void optionsAreHiddenForDraftsAndNonCandleOfferings() throws Exception {
        long draft = createOffering("CANDLE", false);
        long workshop = createOffering("WORKSHOP", true);
        mvc.perform(get("/api/v1/offerings/" + draft + "/options")).andExpect(status().isNotFound());
        mvc.perform(get("/api/v1/offerings/" + workshop + "/options")).andExpect(status().isNotFound());
        mvc.perform(put("/api/v1/admin/offerings/" + workshop + "/options")
            .with(user("admin").roles("ADMIN")).with(csrf())
            .contentType(MediaType.APPLICATION_JSON).content("[]"))
            .andExpect(status().isNotFound());
        mvc.perform(put("/api/v1/admin/offerings/" + draft + "/options").with(csrf())
            .contentType(MediaType.APPLICATION_JSON).content("[]"))
            .andExpect(status().isUnauthorized());
    }
}
