package br.com.lojinhadakatia;

import br.com.lojinhadakatia.offering.repository.OfferingOptionRepository;
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
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("test")
class CandleOptionsIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired OfferingOptionRepository options;
    @Autowired ServiceOfferingRepository offerings;

    @BeforeEach void reset() {
        options.deleteAll();
        offerings.deleteAll();
    }

    private long create(String type) throws Exception {
        String json=mvc.perform(post("/api/v1/admin/offerings")
                .with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                  {"title":"Vela demonstrativa","type":"%s","published":true}
                  """.formatted(type)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        return mapper.readTree(json).get("id").asLong();
    }

    @Test void candleOptionsAreModelSpecificAndValidatedBeforeWhatsApp() throws Exception {
        mvc.perform(put("/api/v1/admin/store")
                .with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"displayName\":\"Lojinha da Kátia\",\"whatsapp\":\"5547999999999\"}"))
            .andExpect(status().isOk());
        long candle=create("CANDLE");
        String url="/api/v1/admin/offerings/"+candle+"/options";
        String config="""
          {"options":[
            {"kind":"AROMA","label":"Lavanda","position":0,"active":true},
            {"kind":"COLOR","label":"Rosa","position":1,"active":true},
            {"kind":"PACKAGING","label":"Caixa","position":2,"active":false}
          ]}
          """;
        mvc.perform(put(url).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(config))
            .andExpect(status().isUnauthorized());
        mvc.perform(put(url).with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(config))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].label").value("Lavanda"));

        mvc.perform(get("/api/v1/offerings/"+candle))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.options.length()").value(2));

        String endpoint="/api/v1/whatsapp/confirm";
        mvc.perform(post(endpoint).contentType(MediaType.APPLICATION_JSON)
                .content("""
                  {"type":"OFFERING","id":%d,"customizations":{"AROMA":"Lavanda","COLOR":"Rosa"}}
                  """.formatted(candle)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message",containsString("AROMA: Lavanda")))
            .andExpect(jsonPath("$.message",containsString("COLOR: Rosa")))
            .andExpect(jsonPath("$.message",containsString("Preço final a confirmar")));
        mvc.perform(post(endpoint).contentType(MediaType.APPLICATION_JSON)
                .content("""
                  {"type":"OFFERING","id":%d,"customizations":{"PACKAGING":"Caixa"}}
                  """.formatted(candle)))
            .andExpect(status().isConflict());
        mvc.perform(post(endpoint).contentType(MediaType.APPLICATION_JSON)
                .content("""
                  {"type":"OFFERING","id":%d,"customizations":{"AROMA":"Inexistente"}}
                  """.formatted(candle)))
            .andExpect(status().isConflict());
    }

    @Test void nonCandleOfferingsRejectCustomizations() throws Exception {
        long workshop=create("WORKSHOP");
        mvc.perform(put("/api/v1/admin/offerings/"+workshop+"/options")
                .with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"options\":[{\"kind\":\"AROMA\",\"label\":\"Lavanda\",\"active\":true}]}"))
            .andExpect(status().isConflict());
        mvc.perform(post("/api/v1/whatsapp/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                  {"type":"OFFERING","id":%d,"customizations":{"AROMA":"Lavanda"}}
                  """.formatted(workshop)))
            .andExpect(status().isConflict());
    }
}
