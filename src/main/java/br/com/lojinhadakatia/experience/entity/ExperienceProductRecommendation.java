package br.com.lojinhadakatia.experience.entity;

import br.com.lojinhadakatia.catalog.entity.Product;
import jakarta.persistence.*;

/** Vínculo comercial ordenado de uma chave de resultado a um produto. */
@Entity @Table(name="experience_product_recommendations")
public class ExperienceProductRecommendation {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false)
    @JoinColumn(name="experience_id",nullable=false)
    private Experience experience;
    @Column(name="result_key",nullable=false,length=80)
    private String resultKey;
    @ManyToOne(fetch=FetchType.LAZY,optional=false)
    @JoinColumn(name="product_id",nullable=false)
    private Product product;
    @Column(nullable=false)
    private int position;
    protected ExperienceProductRecommendation() {}
    public ExperienceProductRecommendation(Experience experience,String resultKey,Product product,int position){
        this.experience=experience;this.resultKey=resultKey;this.product=product;this.position=position;
    }
    public Long getId(){return id;}
    public Product getProduct(){return product;}
}
