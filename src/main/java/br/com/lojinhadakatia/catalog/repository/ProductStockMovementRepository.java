package br.com.lojinhadakatia.catalog.repository;

import br.com.lojinhadakatia.catalog.entity.ProductStockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductStockMovementRepository extends JpaRepository<ProductStockMovement, Long> {
    Page<ProductStockMovement> findByProductIdOrderByIdDesc(Long productId, Pageable pageable);
    boolean existsByProductId(Long productId);
}
