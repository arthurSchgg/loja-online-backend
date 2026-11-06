package br.com.lojinhadakatia.content.repository;
import br.com.lojinhadakatia.content.entity.ContentSection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ContentSectionRepository extends JpaRepository<ContentSection,Long> {
 boolean existsBySlug(String slug);
 List<ContentSection> findByPublishedTrueOrderByPositionAscIdAsc();
}
