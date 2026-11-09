package br.com.lojinhadakatia.catalog.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

/** Promoção de preço em intervalo [startsAt, endsAt), exclusivo no fim. */
@Entity
@Table(name="product_promotions")
public class ProductPromotion {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false)
    @JoinColumn(name="product_id",nullable=false)
    private Product product;
    @Column(name="promotional_price",nullable=false,precision=12,scale=2)
    private BigDecimal promotionalPrice;
    @Column(name="starts_at",nullable=false) private Instant startsAt;
    @Column(name="ends_at",nullable=false) private Instant endsAt;
    @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
    protected ProductPromotion(){}
    public ProductPromotion(Product product,BigDecimal price,Instant starts,Instant ends){
        this.product=product; update(price,starts,ends);
    }
    @PrePersist void created(){if(createdAt==null)createdAt=Instant.now();}
    public void update(BigDecimal price,Instant starts,Instant ends){
        promotionalPrice=price;startsAt=starts;endsAt=ends;
    }
    public Long getId(){return id;}
    public Product getProduct(){return product;}
    public BigDecimal getPromotionalPrice(){return promotionalPrice;}
    public Instant getStartsAt(){return startsAt;}
    public Instant getEndsAt(){return endsAt;}
    public Instant getCreatedAt(){return createdAt;}
}
