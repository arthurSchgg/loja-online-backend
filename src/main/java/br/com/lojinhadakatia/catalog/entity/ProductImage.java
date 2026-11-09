package br.com.lojinhadakatia.catalog.entity;
import jakarta.persistence.*;
/** Imagem externa de produto, ordenada para exibição. */
@Entity @Table(name="product_images")
public class ProductImage {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="product_id",nullable=false) private Product product;
    @Column(name="image_url",nullable=false,length=2048) private String imageUrl;
    @Column(nullable=false) private int position;
    @Column(name="alt_text",length=180) private String altText;
    protected ProductImage() {}
    public ProductImage(String url,int position,String altText){this.imageUrl=url;this.position=position;this.altText=altText;}
    void assignTo(Product product){this.product=product;}
    public Long getId(){return id;} public String getImageUrl(){return imageUrl;}
    public int getPosition(){return position;} public String getAltText(){return altText;}
}
