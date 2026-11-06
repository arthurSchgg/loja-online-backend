package br.com.lojinhadakatia;

import br.com.lojinhadakatia.campaign.repository.CampaignRepository;
import org.junit.jupiter.api.BeforeEach;
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

/** Testes de visibilidade por publicação e janela de datas. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CampaignRegressionTest {

    @Autowired private MockMvc mvc;
    @Autowired private CampaignRepository repository;

    @BeforeEach
    void reset() {
        repository.deleteAll();
    }

    @Test
    void futureCampaignIsInvisibleToVisitors() throws Exception {
        createCampaign("""
            {"title":"Campanha futura","published":true,
             "startsAt":"2099-01-01T00:00:00Z"}
            """);
        mvc.perform(get("/api/v1/campaigns"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void invalidDateWindowIsRejected() throws Exception {
        mvc.perform(post("/api/v1/admin/campaigns")
                .with(httpBasic("tester", "testpassword123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title":"Período inválido","published":true,
                     "startsAt":"2030-01-03T00:00:00Z",
                     "endsAt":"2030-01-02T00:00:00Z"}
                    """))
            .andExpect(status().isConflict());
    }

    @Test
    void liveCampaignIsVisible() throws Exception {
        createCampaign("""
            {"title":"Campanha vigente","published":true}
            """);
        mvc.perform(get("/api/v1/campaigns"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title").value("Campanha vigente"));
    }

    private void createCampaign(String body) throws Exception {
        mvc.perform(post("/api/v1/admin/campaigns")
                .with(httpBasic("tester", "testpassword123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated());
    }
}
