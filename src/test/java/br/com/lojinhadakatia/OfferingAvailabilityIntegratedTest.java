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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OfferingAvailabilityIntegratedTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    @Test
    void publishedUnavailableOfferingsRemainVisibleButCannotBeRequested() throws Exception {
        String create = """
            {"title":"Oficina indisponível","type":"WORKSHOP","published":true,"available":false}
            """;
        var created = mvc.perform(post("/api/v1/admin/offerings")
                .with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(create))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.available").value(false))
            .andReturn();
        long id = mapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        mvc.perform(get("/api/v1/offerings/" + id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.published").value(true))
            .andExpect(jsonPath("$.available").value(false));

        mvc.perform(get("/api/v1/offerings/" + id + "/whatsapp"))
            .andExpect(status().isConflict());

        mvc.perform(post("/api/v1/whatsapp/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"type":"OFFERING","id":%d}
                    """.formatted(id)))
            .andExpect(status().isConflict());

        mvc.perform(put("/api/v1/admin/offerings/" + id)
                .with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title":"Oficina disponível","type":"WORKSHOP","published":true,"available":true}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void legacyUpdateDoesNotSilentlyReactivateUnavailableOffering() throws Exception {
        var response = mvc.perform(post("/api/v1/admin/offerings")
                .with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title":"Vela","type":"CANDLE","published":true,"available":false}
                    """))
            .andExpect(status().isCreated()).andReturn();
        long id=mapper.readTree(response.getResponse().getContentAsString()).get("id").asLong();
        mvc.perform(put("/api/v1/admin/offerings/" + id)
                .with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title":"Vela atualizada","type":"CANDLE","published":true}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.available").value(false));
    }
}
