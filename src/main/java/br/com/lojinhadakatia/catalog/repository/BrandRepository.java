package br.com.lojinhadakatia.catalog.repository;
import br.com.lojinhadakatia.catalog.entity.Brand;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface BrandRepository extends JpaRepository<Brand,Long> {
    boolean existsByNameIgnoreCase(String name);
    List<Brand> findByActiveTrueOrderByNameAsc();
}
