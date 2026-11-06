package br.com.lojinhadakatia.security;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Política de acesso explícita: somente recursos públicos conhecidos podem ser
 * consultados sem autenticação. Qualquer operação administrativa exige ADMIN.
 *
 * HTTP Basic é transitório: deve ser utilizado somente sobre HTTPS e revisado
 * antes da disponibilização pública do painel.
 */
@Configuration
public class SecurityConfiguration {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

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

    @Bean
    CorsConfigurationSource corsSource(@Value("${app.cors.allowed-origins:}") String origins) {
        CorsConfiguration configuration = new CorsConfiguration();
        List<String> allowed = Arrays.stream(origins.split(","))
            .map(String::trim).filter(s -> !s.isBlank()).toList();
        configuration.setAllowedOrigins(allowed);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        configuration.setAllowCredentials(false);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    SecurityFilterChain webSecurity(HttpSecurity http) throws Exception {
        return http
            .cors(Customizer.withDefaults())
            // Não há cookies nem sessões; rever CSRF se o mecanismo mudar.
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
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
                    "/swagger-ui/**", "/swagger-ui.html"
                ).permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/experiences/*/evaluate").permitAll()
                .anyRequest().denyAll())
            .httpBasic(Customizer.withDefaults())
            .build();
    }
}
