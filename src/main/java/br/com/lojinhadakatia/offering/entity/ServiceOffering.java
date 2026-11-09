package br.com.lojinhadakatia.offering.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
/** Oferta publicada: pode ser vela, oficina ou locação, conforme cadastro real. */
@Entity @Table(name="service_offerings")
public class ServiceOffering {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,length=180) private String title;
 @Column(length=4000) private String description;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=30) private OfferingType type;
 @Column(precision=12,scale=2) private BigDecimal price;
 @Column(name="image_url",length=2048) private String imageUrl;
 @Column(nullable=false) private boolean published;
 @Column(nullable=false) private boolean available = true;
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
 @Column(name="updated_at",nullable=false) private Instant updatedAt;
 protected ServiceOffering(){}
 public ServiceOffering(String title,String description,OfferingType type,BigDecimal price,String imageUrl,boolean published){
  update(title,description,type,price,imageUrl,published,null);
 }
 public ServiceOffering(String title,String description,OfferingType type,BigDecimal price,
                        String imageUrl,boolean published,Boolean available){
  update(title,description,type,price,imageUrl,published,available);
 }
 public ServiceOffering(String title,String description,OfferingType type,BigDecimal price,String imageUrl,boolean published,boolean available){
  update(title,description,type,price,imageUrl,published,available);
 }
 @PrePersist void inserted(){createdAt=Instant.now();updatedAt=createdAt;}
 @PreUpdate void updated(){updatedAt=Instant.now();}
 public void update(String title,String description,OfferingType type,BigDecimal price,String imageUrl,boolean published){
  update(title,description,type,price,imageUrl,published,null);
 }
 public void update(String title,String description,OfferingType type,BigDecimal price,
                    String imageUrl,boolean published,Boolean available){
  this.title=title;this.description=description;this.type=type;this.price=price;this.imageUrl=imageUrl;this.published=published;
  if(available!=null)this.available=available;
 }
 public void update(String title,String description,OfferingType type,BigDecimal price,String imageUrl,boolean published,boolean available){
  update(title,description,type,price,imageUrl,published);
  this.available=available;
 }
 public Long getId(){return id;} public String getTitle(){return title;} public String getDescription(){return description;}
 public OfferingType getType(){return type;} public BigDecimal getPrice(){return price;} public String getImageUrl(){return imageUrl;}
 public boolean isPublished(){return published;} public boolean isAvailable(){return available;}
}
