package br.com.lojinhadakatia.security;

import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AdminEmailOutboxRepository extends JpaRepository<AdminEmailOutbox, Long> {
    @Query("select e from AdminEmailOutbox e where e.sentAt is null and e.attempts < 10 and e.nextAttemptAt <= :now order by e.id asc")
    List<AdminEmailOutbox> pending(@Param("now") Instant now, org.springframework.data.domain.Pageable page);
}
