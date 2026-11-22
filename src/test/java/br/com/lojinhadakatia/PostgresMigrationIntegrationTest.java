package br.com.lojinhadakatia;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockMultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Exercita as migrações Flyway e um fluxo de escrita em PostgreSQL real fornecido pelo CI. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@EnabledIfEnvironmentVariable(named="RUN_POSTGRES_INTEGRATION", matches="true")
class PostgresMigrationIntegrationTest {

    @DynamicPropertySource
    static void postgres(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> System.getenv("POSTGRES_TEST_URL"));
        registry.add("spring.datasource.username", () -> System.getenv("POSTGRES_TEST_USER"));
        registry.add("spring.datasource.password", () -> System.getenv("POSTGRES_TEST_PASSWORD"));
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.flyway.enabled", () -> "true");
    }

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    @Test
    void flywayMigratesPostgresAndAdminCanWrite() throws Exception {
        mvc.perform(get("/api/v1/products"))
            .andExpect(status().isOk());
        mvc.perform(post("/api/v1/admin/brands")
                .with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Marca Integracao\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Marca Integracao"));
        var bytes=new ByteArrayOutputStream();
        ImageIO.write(new BufferedImage(50,50,BufferedImage.TYPE_INT_RGB),"jpeg",bytes);
        var photo=mapper.readTree(mvc.perform(multipart("/api/v1/admin/media")
            .file(new MockMultipartFile("file","fixture.jpg","image/jpeg",bytes.toByteArray()))
            .with(user("tester").roles("ADMIN")).with(csrf())).andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString()).get("url").asText();
        mvc.perform(get(photo)).andExpect(status().isNotFound());
        var offer=mapper.readTree(mvc.perform(post("/api/v1/admin/offerings").with(user("tester").roles("ADMIN")).with(csrf())
            .contentType(MediaType.APPLICATION_JSON).content("""
              {"title":"Vela Postgres fixture","type":"CANDLE","published":true,
              "images":[{"url":"%s","position":0}]}
              """.formatted(photo))).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString());
        mvc.perform(get(photo)).andExpect(status().isOk()).andExpect(content().contentType("image/jpeg"));
        mvc.perform(get("/api/v1/offerings/"+offer.get("id").asLong())).andExpect(status().isOk())
            .andExpect(jsonPath("$.imageUrl").value(photo));
        mvc.perform(get("/api/v1/storefront/featured")).andExpect(status().isOk());
        mvc.perform(put("/api/v1/admin/whatsapp-templates/PRODUCT").with(user("tester").roles("ADMIN")).with(csrf())
            .contentType(MediaType.APPLICATION_JSON).content("{\"template\":\"Olá {{nome}}: {{preco}}\"}"))
            .andExpect(status().isOk());
    }
}
