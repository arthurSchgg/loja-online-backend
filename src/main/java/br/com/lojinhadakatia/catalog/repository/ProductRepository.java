package br.com.lojinhadakatia.catalog.repository;
import br.com.lojinhadakatia.catalog.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
public interface ProductRepository extends JpaRepository<Product,Long>,JpaSpecificationExecutor<Product> {}
