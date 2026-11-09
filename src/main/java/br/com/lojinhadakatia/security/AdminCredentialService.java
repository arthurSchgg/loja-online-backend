package br.com.lojinhadakatia.security;

import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * One persistent administrator. Environment credentials are used ONLY if
 * the database has no administrator; restarts never overwrite a changed hash.
 */
@Service
public class AdminCredentialService implements UserDetailsService {
    private final AdminCredentialRepository repository;
    private final PasswordEncoder encoder;

    public AdminCredentialService(AdminCredentialRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    @Bean
    ApplicationRunner bootstrapAdmin(AdminCredentialService service,
            @Value("${app.security.admin-username:}") String username,
            @Value("${app.security.admin-password:}") String password,
            @Value("${app.security.admin-email:}") String email) {
        return args -> service.initialize(username, password, email);
    }

    @Transactional
    public void initialize(String username, String password, String email) {
        if (repository.existsById(1L)) return;
        if (username == null || username.isBlank() || password == null
                || password.length() < 12 || password.length() > 128) {
            throw new IllegalStateException("ADMIN_USERNAME e ADMIN_PASSWORD (12-128 caracteres) obrigatórios para bootstrap.");
        }
        repository.saveAndFlush(new AdminCredential(username.trim(),
            email == null || email.isBlank() ? null : email.trim().toLowerCase(java.util.Locale.ROOT),
            encoder.encode(password)));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        return repository.findByUsername(username)
            .map(admin -> User.withUsername(admin.getUsername())
                .password(admin.getPasswordHash()).roles("ADMIN").build())
            .orElseThrow(() -> new UsernameNotFoundException("Credenciais inválidas."));
    }

    @Transactional(readOnly = true)
    public long credentialVersion(String username) {
        return repository.findByUsername(username)
            .map(AdminCredential::getCredentialVersion).orElse(-1L);
    }

    @Transactional
    public void changePassword(String username, String current, String next, String confirmation) {
        AdminCredential admin = repository.lockAdministrator()
            .orElseThrow(() -> new IllegalStateException("Administrador não configurado."));
        if (!Objects.equals(admin.getUsername(), username) || !encoder.matches(current, admin.getPasswordHash()))
            throw new org.springframework.security.authentication.BadCredentialsException("Senha atual inválida.");
        if (next == null || next.length() < 12 || next.length() > 128)
            throw new IllegalArgumentException("Nova senha deve conter de 12 a 128 caracteres.");
        if (!next.equals(confirmation))
            throw new IllegalArgumentException("Confirmação da senha não corresponde.");
        if (encoder.matches(next, admin.getPasswordHash()))
            throw new IllegalArgumentException("Escolha uma senha diferente da atual.");
        admin.changePassword(encoder.encode(next));
        repository.saveAndFlush(admin);
    }
}
