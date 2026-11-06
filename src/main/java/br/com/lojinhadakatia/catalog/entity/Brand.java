package br.com.lojinhadakatia.catalog.entity;
import jakarta.persistence.*;
/** Marca comercial, ativável sem excluir seu histórico. */
@Entity @Table(name="brands")
public class Brand {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, unique=true, length=120) private String name;
    @Column(nullable=false) private boolean active=true;
    protected Brand() {}
    public Brand(String name) { this.name=name; }
    public Long getId(){return id;} public String getName(){return name;} public boolean isActive(){return active;}
    public void rename(String name){this.name=name;} public void setActive(boolean value){active=value;}
}
