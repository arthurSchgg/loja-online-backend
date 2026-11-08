package br.com.lojinhadakatia.offering.repository;

import br.com.lojinhadakatia.offering.entity.CandleOption;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandleOptionRepository extends JpaRepository<CandleOption, Long> {
    List<CandleOption> findByOfferingIdOrderByKindAscValueAsc(Long offeringId);
    List<CandleOption> findByOfferingIdAndActiveTrueOrderByKindAscValueAsc(Long offeringId);
}
