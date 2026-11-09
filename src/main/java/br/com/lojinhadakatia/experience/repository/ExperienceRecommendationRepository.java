package br.com.lojinhadakatia.experience.repository;

import br.com.lojinhadakatia.experience.entity.ExperienceRecommendation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExperienceRecommendationRepository extends JpaRepository<ExperienceRecommendation, Long> {
    List<ExperienceRecommendation> findByResultIdOrderByPositionAscIdAsc(Long resultId);
    void deleteByResultId(Long resultId);
}
