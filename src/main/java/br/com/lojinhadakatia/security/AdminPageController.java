package br.com.lojinhadakatia.security;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Unlisted entry points for the same-origin admin UI; authorization remains on every API. */
@RestController
@io.swagger.v3.oas.annotations.Hidden
public class AdminPageController {
    public static final String LOGIN = "/gestao-proprietaria-k73m";
    public static final String EXPERIENCES = "/gestao-experiencias-k73m";

    @GetMapping(LOGIN)
    public ResponseEntity<Resource> login() {
        return page("admin.html");
    }

    @GetMapping(EXPERIENCES)
    public ResponseEntity<Resource> experiences() {
        return page("admin-experiences.html");
    }

    private ResponseEntity<Resource> page(String file) {
        return ResponseEntity.ok()
            .contentType(MediaType.TEXT_HTML)
            .cacheControl(CacheControl.noStore())
            .header("X-Robots-Tag", "noindex, nofollow, noarchive")
            .header("Referrer-Policy", "no-referrer")
            .body(new ClassPathResource("static/" + file));
    }
}
