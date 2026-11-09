package br.com.lojinhadakatia.security.audit;

import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AdminAuditEventRepository extends JpaRepository<AdminAuditEvent, Long> {
    @Query("""
        select e from AdminAuditEvent e
        where (:actor is null or e.actor = :actor)
          and (:route is null or e.routePattern = :route)
          and (:fromTime is null or e.occurredAt >= :fromTime)
          and (:toTime is null or e.occurredAt < :toTime)
        """)
    Page<AdminAuditEvent> findFiltered(
        @Param("actor") String actor,
        @Param("route") String route,
        @Param("fromTime") Instant fromTime,
        @Param("toTime") Instant toTime,
        Pageable pageable
    );
}
