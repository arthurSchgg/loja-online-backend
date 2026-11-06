package br.com.lojinhadakatia.security;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.*;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

/**
 * Segurança administrativa provisória: HTTP Basic stateless com BCrypt.
 * Exige HTTPS no deploy. JWT/sessão dependem de decisão de arquitetura.
 * Nenhuma operação de escrita é pública.
 */
@Configuration
public class SecurityConfiguration {
    @Bean
    PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
    @Bean
    UserDetailsService administrators(@Value("${app.security.admin-username:}") String username,
                                      @Value("${app.security.admin-password:}") String password,
                                      PasswordEncoder encoder) {
        if(username.isBlank() || password.length()<12) {
            throw new IllegalStateException("Configure ADMIN_USERNAME e ADMIN_PASSWORD (mínimo 12 caracteres).");
        }
        UserDetails admin=User.withUsername(username).password(encoder.encode(password)).roles("ADMIN").build();
        return new InMemoryUserDetailsManager(admin);
    }
    @Bean
    CorsConfigurationSource corsSource(@Value("${app.cors.allowed-origins:}") String origins) {
        CorsConfiguration configuration=new CorsConfiguration();
        List<String> allowed=Arrays.stream(origins.split(",")).map(String::trim).filter(s->!s.isBlank()).toList();
        configuration.setAllowedOrigins(allowed);
        configuration.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization","Content-Type","Accept"));
        configuration.setAllowCredentials(false);
        UrlBasedCorsConfigurationSource source=new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",configuration);
        return source;
    }
    @Bean
    SecurityFilterChain webSecurity(HttpSecurity http) throws Exception {
        return http
            .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth->auth
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET,"/api/v1/**","/actuator/health","/actuator/info",
                    "/v3/api-docs/**","/swagger-ui/**","/swagger-ui.html").permitAll()
                .anyRequest().denyAll())
            .httpBasic(Customizer.withDefaults())
            .build();
    }
}
