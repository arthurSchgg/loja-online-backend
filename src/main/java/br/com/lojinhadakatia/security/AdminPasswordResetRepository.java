package br.com.lojinhadakatia.security;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminPasswordResetRepository extends JpaRepository<AdminPasswordResetToken, Long> {
    Optional<AdminPasswordResetToken> findByTokenHash(String tokenHash);
}
