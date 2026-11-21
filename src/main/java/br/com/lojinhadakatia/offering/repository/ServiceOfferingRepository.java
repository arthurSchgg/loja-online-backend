package br.com.lojinhadakatia.offering.repository;
import br.com.lojinhadakatia.offering.entity.*;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
public interface ServiceOfferingRepository extends JpaRepository<ServiceOffering,Long> {
 List<ServiceOffering> findByPublishedTrueOrderByIdDesc();
 List<ServiceOffering> findByPublishedTrueAndTypeOrderByIdDesc(OfferingType type);
 @Lock(LockModeType.PESSIMISTIC_WRITE)
 @Query("select o from ServiceOffering o where o.id=:id")
 Optional<ServiceOffering> findByIdForUpdate(@Param("id") Long id);
}
