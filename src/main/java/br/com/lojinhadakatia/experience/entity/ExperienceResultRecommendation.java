package br.com.lojinhadakatia.experience.entity;

import br.com.lojinhadakatia.catalog.entity.Product;
import jakarta.persistence.*;

@Entity
@Table(name = "experience_result_recommendations")
public class ExperienceResultRecommendation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "result_id", nullable = false)
    private ExperienceResult result;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    @Column(nullable = false)
    private int position;
    protected ExperienceResultRecommendation() {}
    public ExperienceResultRecommendation(ExperienceResult result, Product product, int position) {
        this.result = result;
        this.product = product;
        this.position = position;
    }
    public Long getId() { return id; }
    public Product getProduct() { return product; }
    public int getPosition() { return position; }
}
