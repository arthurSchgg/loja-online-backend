package br.com.lojinhadakatia.storefront;

import jakarta.persistence.*;

@Entity @Table(name="featured_products")
public class FeaturedProduct {
    @Id @Column(name="product_id") private Long productId;
    @Column(nullable=false,unique=true) private int position;
    protected FeaturedProduct(){}
}
