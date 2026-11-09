package br.com.lojinhadakatia.experience.repository;
import br.com.lojinhadakatia.experience.entity.Experience;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ExperienceRepository extends JpaRepository<Experience,Long> {
 List<Experience> findByPublishedTrueOrderByIdDesc();
}
