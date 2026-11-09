package br.com.lojinhadakatia;

import br.com.lojinhadakatia.content.repository.ContentSectionRepository;
import br.com.lojinhadakatia.offering.repository.ServiceOfferingRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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

/** Garante que conteúdos e serviços em rascunho não vazem na vitrine. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OfferingAndContentRegressionTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ServiceOfferingRepository offerings;
    @Autowired private ContentSectionRepository sections;

    @BeforeEach
    void reset() {
        offerings.deleteAll();
        sections.deleteAll();
    }

    @Test
    void publicListDoesNotRevealUnpublishedOffering() throws Exception {
        long id = createdOffering(false);
        mvc.perform(get("/api/v1/offerings/" + id))
            .andExpect(status().isNotFound());
        mvc.perform(get("/api/v1/offerings?type=CANDLE"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void publishedCandleCanBeFilteredByType() throws Exception {
        createdOffering(true);
        mvc.perform(get("/api/v1/offerings?type=CANDLE"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title").value("Vela de demonstração"))
            .andExpect(jsonPath("$[0].price").value(29.90));
        mvc.perform(get("/api/v1/offerings?type=WORKSHOP"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void negativePriceIsRejected() throws Exception {
        mvc.perform(post("/api/v1/admin/offerings")
                .with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title":"Valor inválido","type":"CANDLE","price":-1,"published":true}
                    """))
            .andExpect(status().isBadRequest());
    }

    @Test
    void institutionalDraftIsHiddenAndPublishedContentIsVisible() throws Exception {
        String payload = """
            {"slug":"quem-somos","title":"Conheça a loja",
             "body":"Conteúdo de teste","position":1,"published":false}
            """;
        mvc.perform(post("/api/v1/admin/content-sections")
                .with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isCreated());

        mvc.perform(get("/api/v1/content-sections"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());

        mvc.perform(get("/api/v1/admin/content-sections")
                .with(user("tester").roles("ADMIN")).with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].slug").value("quem-somos"));
    }

    private long createdOffering(boolean published) throws Exception {
        String response = mvc.perform(post("/api/v1/admin/offerings")
                .with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title":"Vela de demonstração","type":"CANDLE",
                     "price":29.90,"published":%s}
                    """.formatted(published)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(response);
        return node.get("id").asLong();
    }
}
