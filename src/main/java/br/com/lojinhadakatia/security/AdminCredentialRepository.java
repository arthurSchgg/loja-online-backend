package br.com.lojinhadakatia.security;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

public interface AdminCredentialRepository extends JpaRepository<AdminCredential, Long> {
    Optional<AdminCredential> findByUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select a from AdminCredential a where a.id = 1")
    Optional<AdminCredential> lockAdministrator();
}
