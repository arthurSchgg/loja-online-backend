package br.com.lojinhadakatia.catalog.repository;
import br.com.lojinhadakatia.catalog.entity.Category;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CategoryRepository extends JpaRepository<Category,Long> {
    boolean existsByNameIgnoreCase(String name);
    List<Category> findByActiveTrueOrderByNameAsc();
}
