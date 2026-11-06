package br.com.lojinhadakatia.offering.repository;
import br.com.lojinhadakatia.offering.entity.*;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ServiceOfferingRepository extends JpaRepository<ServiceOffering,Long> {
 List<ServiceOffering> findByPublishedTrueOrderByIdDesc();
 List<ServiceOffering> findByPublishedTrueAndTypeOrderByIdDesc(OfferingType type);
}
