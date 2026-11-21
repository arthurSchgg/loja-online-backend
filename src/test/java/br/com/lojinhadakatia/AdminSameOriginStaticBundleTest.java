package br.com.lojinhadakatia;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Keeping the Render admin bundle in develop prevents losing it in a future release. */
@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("test")
class AdminSameOriginStaticBundleTest {
    @Autowired MockMvc mvc;

    @Test void shipsSameOriginHtmlAndAccountRecovery() throws Exception {
        mvc.perform(get("/admin.html")).andExpect(status().isOk())
            .andExpect(content().string(containsString("login-form")))
            .andExpect(content().string(containsString("https://main.loja-online-frontend.pages.dev/")));
        mvc.perform(get("/admin-experiences.html")).andExpect(status().isOk());
        mvc.perform(get("/reset-password.html")).andExpect(status().isOk());
        mvc.perform(get("/confirm-email.html")).andExpect(status().isOk());
        mvc.perform(get("/admin/admin.js")).andExpect(status().isOk())
            .andExpect(content().string(containsString("manualAvailable")));
        mvc.perform(get("/admin/api.js")).andExpect(status().isOk())
            .andExpect(content().string(containsString("credentials:\"same-origin\"")));
        mvc.perform(get("/assets/css/base.css")).andExpect(status().isOk());
        mvc.perform(get("/assets/images/brand/logo-katia.svg")).andExpect(status().isOk());
    }

    @Test void noAnonymousAdminDataOrUnintendedPublicFiles() throws Exception {
        mvc.perform(get("/api/v1/admin/products")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/v1/auth/me")).andExpect(status().isUnauthorized());
        mvc.perform(get("/application.yml")).andExpect(status().isUnauthorized());
        mvc.perform(get("/index.html")).andExpect(status().isUnauthorized());
        mvc.perform(get("/assets/js/public-catalog-snapshot.js")).andExpect(status().isUnauthorized());
    }
}
