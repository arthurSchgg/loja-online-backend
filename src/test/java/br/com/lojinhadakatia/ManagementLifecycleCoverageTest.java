package br.com.lojinhadakatia;

import br.com.lojinhadakatia.campaign.repository.CampaignRepository;
import br.com.lojinhadakatia.content.repository.ContentSectionRepository;
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

/** Integration regression coverage for admin CRUD and defensive validation paths. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ManagementLifecycleCoverageTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired CampaignRepository campaigns;
    @Autowired ContentSectionRepository sections;
    @Autowired ServiceOfferingRepository offerings;

    @BeforeEach
    void clear() {
        campaigns.deleteAll();
        sections.deleteAll();
        offerings.deleteAll();
    }

    private long create(String endpoint, String payload) throws Exception {
        String result = mvc.perform(post(endpoint).with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(payload))
            .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return mapper.readTree(result).get("id").asLong();
    }

    private void put(String endpoint, String body, int statusCode) throws Exception {
        mvc.perform(put(endpoint).with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().is(statusCode));
    }

    private void remove(String endpoint, int statusCode) throws Exception {
        mvc.perform(delete(endpoint).with(user("tester").roles("ADMIN")).with(csrf()))
            .andExpect(status().is(statusCode));
    }

    @Test
    void contentLifecycleRejectsSlugModificationAndDeletionOfPublishedItem() throws Exception {
        String base = "/api/v1/admin/content-sections";
        long id = create(base, """
            {"slug":"quem-somos","title":"Nossa loja","body":"Texto","position":1,"published":true}
            """);
        mvc.perform(get(base).with(user("tester").roles("ADMIN")))
            .andExpect(status().isOk()).andExpect(jsonPath("$[0].slug").value("quem-somos"));
        mvc.perform(get("/api/v1/content-sections"))
            .andExpect(status().isOk()).andExpect(jsonPath("$[0].slug").value("quem-somos"));
        put(base + "/" + id, """
            {"slug":"quem-somos","title":"Atualizado","body":"Nova versão","position":2,"published":true}
            """, 200);
        put(base + "/" + id, """
            {"slug":"outro-slug","title":"Renomeado","position":1,"published":true}
            """, 409);
        remove(base + "/" + id, 409);
        put(base + "/" + id, """
            {"slug":"quem-somos","title":"Rascunho","position":1,"published":false}
            """, 200);
        remove(base + "/" + id, 204);
        remove(base + "/" + id, 404);
    }

    @Test
    void contentRejectsDuplicateSlugAndNonHttpsImages() throws Exception {
        String endpoint = "/api/v1/admin/content-sections";
        create(endpoint, """
            {"slug":"entrega","title":"Entrega","position":0,"published":false}
            """);
        mvc.perform(post(endpoint).with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"slug":"entrega","title":"Duplicado","position":0,"published":false}
                    """))
            .andExpect(status().isConflict());
        for (String image : new String[] {"http://example.com/image.jpg", "https://user:pass@example.com/ok.jpg"}) {
            mvc.perform(post(endpoint).with(user("tester").roles("ADMIN")).with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"slug":"novo-item","title":"Imagem","position":0,"published":false,"imageUrl":"%s"}
                        """.formatted(image)))
                .andExpect(status().isConflict());
        }
    }

    @Test
    void offeringLifecycleEnforcesPublishAndVisibilityRules() throws Exception {
        String base = "/api/v1/admin/offerings";
        long id = create(base, """
            {"title":"Oficina","type":"WORKSHOP","price":null,"published":true}
            """);
        mvc.perform(get(base + "/" + id).with(user("tester").roles("ADMIN")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.title").value("Oficina"));
        mvc.perform(get(base).with(user("tester").roles("ADMIN")))
            .andExpect(status().isOk()).andExpect(jsonPath("$[0].type").value("WORKSHOP"));
        mvc.perform(get("/api/v1/offerings/" + id))
            .andExpect(status().isOk()).andExpect(jsonPath("$.title").value("Oficina"));
        mvc.perform(get("/api/v1/offerings"))
            .andExpect(status().isOk()).andExpect(jsonPath("$[0].title").value("Oficina"));
        remove(base + "/" + id, 409);
        put(base + "/" + id, """
            {"title":"Oficina arquivada","type":"WORKSHOP","published":false}
            """, 200);
        mvc.perform(get("/api/v1/offerings/" + id)).andExpect(status().isNotFound());
        remove(base + "/" + id, 204);
        mvc.perform(get(base + "/" + id).with(user("tester").roles("ADMIN")))
            .andExpect(status().isNotFound());
    }

    @Test
    void offeringRejectsInsecureImageAndUpdateOfUnknownItem() throws Exception {
        String base = "/api/v1/admin/offerings";
        mvc.perform(post(base).with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title":"Oferta inválida","type":"CANDLE","imageUrl":"http://example.com/i.jpg"}
                    """))
            .andExpect(status().isConflict());
        put(base + "/999999", """
            {"title":"Oferta inexistente","type":"CANDLE","published":false}
            """, 404);
    }

    @Test
    void campaignLifecycleValidatesUrlsAndDeletionRules() throws Exception {
        String base = "/api/v1/admin/campaigns";
        long id = create(base, """
            {"title":"Destaque","published":true,"position":1,"linkUrl":"https://example.com/"}
            """);
        mvc.perform(get(base).with(user("tester").roles("ADMIN")))
            .andExpect(status().isOk()).andExpect(jsonPath("$[0].title").value("Destaque"));
        put(base + "/" + id, """
            {"title":"Destaque novo","published":true,"position":2,"imageUrl":"https://example.com/cover.png"}
            """, 200);
        remove(base + "/" + id, 409);
        put(base + "/" + id, """
            {"title":"Destaque arquivado","published":false,"position":2}
            """, 200);
        remove(base + "/" + id, 204);
        remove(base + "/" + id, 404);
        mvc.perform(post(base).with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title":"Link inválido","linkUrl":"http://example.com","published":false}
                    """))
            .andExpect(status().isConflict());
        mvc.perform(post(base).with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title":"Imagem inválida","imageUrl":"https://user:secret@example.com/a.png","published":false}
                    """))
            .andExpect(status().isConflict());
    }
}
