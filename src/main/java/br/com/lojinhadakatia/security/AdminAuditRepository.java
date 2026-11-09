package br.com.lojinhadakatia.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** Audit history is append-only via the application service. */
public interface AdminAuditRepository extends JpaRepository<AdminAuditEvent, Long>, JpaSpecificationExecutor<AdminAuditEvent> {
    Page<AdminAuditEvent> findAllByOrderByCreatedAtDescIdDesc(Pageable pageable);
}
