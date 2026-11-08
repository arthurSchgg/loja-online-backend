package br.com.lojinhadakatia.experience.repository;

import br.com.lojinhadakatia.experience.entity.ExperienceProductRecommendation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExperienceProductRecommendationRepository extends JpaRepository<ExperienceProductRecommendation,Long> {
    List<ExperienceProductRecommendation> findByExperienceIdAndResultKeyOrderByPositionAscIdAsc(Long experienceId,String resultKey);
    void deleteByExperienceIdAndResultKey(Long experienceId,String resultKey);
    void deleteByExperienceId(Long experienceId);
    boolean existsByProductId(Long productId);
}
