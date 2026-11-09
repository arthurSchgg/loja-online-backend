package br.com.lojinhadakatia.offering.repository;

import br.com.lojinhadakatia.offering.entity.OfferingOption;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfferingOptionRepository extends JpaRepository<OfferingOption, Long> {
    List<OfferingOption> findByOfferingIdOrderByPositionAscIdAsc(Long offeringId);
    void deleteByOfferingId(Long offeringId);
}
