package br.com.lojinhadakatia.store.repository;
import br.com.lojinhadakatia.store.entity.StoreInformation;
import org.springframework.data.jpa.repository.JpaRepository;
public interface StoreInformationRepository extends JpaRepository<StoreInformation,Long> {}
