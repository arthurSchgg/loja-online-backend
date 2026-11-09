package br.com.lojinhadakatia.catalog.entity;
import jakarta.persistence.*;
/** Agrupamento temático utilizado na vitrine e nos filtros. */
@Entity @Table(name="categories")
public class Category {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false,unique=true,length=120) private String name;
    @Column(nullable=false) private boolean active=true;
    protected Category() {}
    public Category(String name){this.name=name;}
    public Long getId(){return id;} public String getName(){return name;} public boolean isActive(){return active;}
    public void rename(String value){name=value;} public void setActive(boolean value){active=value;}
}
