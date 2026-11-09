package br.com.lojinhadakatia.catalog.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
/** Produto: preço de referência exibido na vitrine, não um pedido ou estoque transacional. */
@Entity @Table(name="products")
public class Product {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false,length=180) private String name;
    @Column(length=4000) private String description;
    @Column(nullable=false,precision=12,scale=2) private BigDecimal price;
    @Column(nullable=false) private boolean available=true;
    @Column(nullable=false) private boolean published=false;
    @Column(name="stock_quantity", nullable=false) private int stockQuantity=0;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="brand_id",nullable=false) private Brand brand;
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="product_categories",joinColumns=@JoinColumn(name="product_id"),inverseJoinColumns=@JoinColumn(name="category_id"))
    private Set<Category> categories=new LinkedHashSet<>();
    @OneToMany(mappedBy="product",cascade=CascadeType.ALL,orphanRemoval=true)
    @OrderBy("position ASC,id ASC") private List<ProductImage> images=new ArrayList<>();
    @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
    @Column(name="updated_at",nullable=false) private Instant updatedAt;
    protected Product() {}
    public Product(String name,String description,BigDecimal price,Brand brand){
        this.name=name;this.description=description;this.price=price;this.brand=brand;
    }
    @PrePersist void inserted(){createdAt=Instant.now();updatedAt=createdAt;}
    @PreUpdate void updated(){updatedAt=Instant.now();}
    public Long getId(){return id;} public String getName(){return name;} public String getDescription(){return description;}
    public BigDecimal getPrice(){return price;} public int getStockQuantity(){return stockQuantity;}
    public void setStockQuantity(int quantity){if(quantity<0)throw new IllegalArgumentException("Estoque negativo.");this.stockQuantity=quantity;} public boolean isAvailable(){return available;} public boolean isPublished(){return published;}
    public Brand getBrand(){return brand;} public Set<Category> getCategories(){return categories;} public List<ProductImage> getImages(){return images;}
    public Instant getCreatedAt(){return createdAt;} public Instant getUpdatedAt(){return updatedAt;}
    public void update(String name,String description,BigDecimal price,Brand brand,Set<Category> categories,boolean available,boolean published){
        this.name=name;this.description=description;this.price=price;this.brand=brand;
        this.categories.clear();this.categories.addAll(categories);this.available=available;this.published=published;
    }
    public void replaceImages(List<ProductImage> replacements){
        images.clear();
        replacements.forEach(image->{image.assignTo(this);images.add(image);});
    }
}
