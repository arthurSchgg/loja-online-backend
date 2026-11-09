package br.com.lojinhadakatia.security;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminEmailChangeTokenRepository extends JpaRepository<AdminEmailChangeToken, Long> {
    Optional<AdminEmailChangeToken> findByTokenHash(String tokenHash);
}
