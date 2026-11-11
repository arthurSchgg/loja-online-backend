package br.com.lojinhadakatia.catalog.repository;

import br.com.lojinhadakatia.catalog.entity.ProductPromotion;
import java.time.Instant;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface ProductPromotionRepository extends JpaRepository<ProductPromotion,Long> {
    List<ProductPromotion> findByProductIdOrderByStartsAtAsc(Long productId);
    boolean existsByProductId(Long productId);

    @Query("""
        select count(p) from ProductPromotion p where p.product.id=:productId
        and p.closedAt is null and p.startsAt<:endAt and p.endsAt>:startAt
        and (:excludeId is null or p.id<>:excludeId)
        """)
    long countOverlaps(@Param("productId") Long productId,
                       @Param("startAt") Instant startAt,@Param("endAt") Instant endAt,
                       @Param("excludeId") Long excludeId);

    @Query("""
        select p from ProductPromotion p where p.product.id=:productId
        and p.closedAt is null and p.startsAt<=:now and p.endsAt>:now
        """)
    List<ProductPromotion> findEffective(@Param("productId") Long productId,@Param("now") Instant now);
}
