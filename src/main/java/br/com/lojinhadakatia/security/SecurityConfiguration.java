package br.com.lojinhadakatia.security;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Autenticação do único administrador por sessão HTTP, protegida por CSRF.
 *
 * O frontend administrativo e a API devem compartilhar a mesma origem.
 * Não existe HTTP Basic, JWT ou cadastro público. Todas as outras rotas
 * permanecem negadas por padrão.
 */
@Configuration
public class SecurityConfiguration {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Credenciais injetadas do ambiente, nunca persistidas nem versionadas.
     * Uma única conta é adequada ao escopo aprovado pela proprietária.
     */
    @Bean
    UserDetailsService administrators(
            @Value("${app.security.admin-username:}") String username,
            @Value("${app.security.admin-password:}") String password,
            PasswordEncoder encoder) {
        if (username.isBlank() || password.length() < 12) {
            throw new IllegalStateException(
                "Configure ADMIN_USERNAME e ADMIN_PASSWORD (mínimo 12 caracteres).");
        }
        UserDetails admin = User.withUsername(username)
            .password(encoder.encode(password))
            .roles("ADMIN")
            .build();
        return new InMemoryUserDetailsManager(admin);
    }

    /**
     * CORS permite somente leitura pública sem credenciais em origens externas.
     * Escritas e sessão administrativa devem usar a mesma origem da API.
     */
    @Bean
    CorsConfigurationSource corsSource(@Value("${app.cors.allowed-origins:}") String origins) {
        CorsConfiguration configuration = new CorsConfiguration();
        List<String> allowed = Arrays.stream(origins.split(","))
            .map(String::trim).filter(s -> !s.isBlank()).toList();
        configuration.setAllowedOrigins(allowed);
        configuration.setAllowedMethods(List.of("GET", "HEAD", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Content-Type", "Accept"));
        configuration.setAllowCredentials(false);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Match specific stateless POST paths before the general GET-only rule.
        // Public POST endpoints are stateless and accept no credentials from external sites.
        CorsConfiguration publicPost = new CorsConfiguration();
        publicPost.setAllowedOrigins(allowed);
        publicPost.setAllowedMethods(List.of("POST", "OPTIONS"));
        publicPost.setAllowedHeaders(List.of("Content-Type", "Accept"));
        publicPost.setAllowCredentials(false);
        source.registerCorsConfiguration("/api/v1/whatsapp/confirm", publicPost);
        source.registerCorsConfiguration("/api/v1/experiences/*/evaluate", publicPost);
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    SecurityFilterChain webSecurity(HttpSecurity http) throws Exception {
        HttpSessionCsrfTokenRepository csrfRepository = new HttpSessionCsrfTokenRepository();
        csrfRepository.setHeaderName("X-CSRF-TOKEN");

        return http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf
                .csrfTokenRepository(csrfRepository)
                // Quiz público: avaliação de leitura, sem persistência ou alteração de estado.
                .ignoringRequestMatchers("/api/v1/experiences/*/evaluate", "/api/v1/whatsapp/confirm"))
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .sessionFixation(fixation -> fixation.changeSessionId()))
            .requestCache(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/v1/auth/csrf").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/auth/me").authenticated()
                .requestMatchers(HttpMethod.GET,
                    "/api/v1/products", "/api/v1/products/*",
                    "/api/v1/products/*/whatsapp",
                    "/api/v1/brands", "/api/v1/categories", "/api/v1/store",
                    "/api/v1/experiences", "/api/v1/experiences/*",
                    "/api/v1/offerings", "/api/v1/offerings/*",
                    "/api/v1/offerings/*/whatsapp",
                    "/api/v1/content-sections", "/api/v1/campaigns",
                    "/actuator/health", "/actuator/info",
                    "/v3/api-docs", "/v3/api-docs/**",
                    "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/experiences/*/evaluate", "/api/v1/whatsapp/confirm").permitAll()
                .anyRequest().denyAll())
            .formLogin(form -> form
                .loginProcessingUrl("/api/v1/auth/login")
                .successHandler((request, response, authentication) -> {
                    response.setStatus(200);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.getWriter().write("{\"authenticated\":true}");
                })
                .failureHandler((request, response, exception) ->
                    response.sendError(401, "Credenciais inválidas."))
                .permitAll())
            .logout(logout -> logout
                .logoutUrl("/api/v1/auth/logout")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessHandler((request, response, authentication) ->
                    response.setStatus(204)))
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint((request, response, exception) ->
                    response.sendError(401, "Autenticação necessária."))
                .accessDeniedHandler((request, response, exception) ->
                    response.sendError(403, "Acesso negado.")))
            .httpBasic(AbstractHttpConfigurer::disable)
            .build();
    }
}
