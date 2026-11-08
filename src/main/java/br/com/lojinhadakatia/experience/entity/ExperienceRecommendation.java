package br.com.lojinhadakatia.experience.entity;

import br.com.lojinhadakatia.catalog.entity.Product;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/** Manual suggestion attached to a quiz result. Eligibility is checked when evaluated. */
@Entity
@Table(name = "experience_recommendations")
public class ExperienceRecommendation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "result_id", nullable = false)
    private ExperienceResult result;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    @Column(nullable = false)
    private int position;

    protected ExperienceRecommendation() {}
    public ExperienceRecommendation(ExperienceResult result, Product product, int position) {
        this.result = result;
        this.product = product;
        this.position = position;
    }
    public Long getId() { return id; }
    public Product getProduct() { return product; }
    public int getPosition() { return position; }
}
