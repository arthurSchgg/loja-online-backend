package br.com.lojinhadakatia.store.entity;
import jakarta.persistence.*;
import java.time.Instant;
/** Conteúdo institucional editável; nenhum contato real é pré-preenchido. */
@Entity @Table(name="store_information")
public class StoreInformation {
    @Id private Long id=1L;
    @Column(name="display_name",nullable=false,length=180) private String displayName;
    @Column(length=240) private String slogan;
    @Column(length=4000) private String description;
    @Column(length=20) private String whatsapp;
    @Column(name="instagram_url",length=2048) private String instagramUrl;
    @Column(length=254) private String email;
    @Column(name="updated_at",nullable=false) private Instant updatedAt;
    protected StoreInformation(){}
    public StoreInformation(String displayName,String slogan,String description,String whatsapp,String instagramUrl,String email){
        update(displayName,slogan,description,whatsapp,instagramUrl,email);
    }
    public void update(String displayName,String slogan,String description,String whatsapp,String instagramUrl,String email){
        this.displayName=displayName;this.slogan=slogan;this.description=description;
        this.whatsapp=whatsapp;this.instagramUrl=instagramUrl;this.email=email;updatedAt=Instant.now();
    }
    public Long getId(){return id;} public String getDisplayName(){return displayName;}
    public String getSlogan(){return slogan;} public String getDescription(){return description;}
    public String getWhatsapp(){return whatsapp;} public String getInstagramUrl(){return instagramUrl;}
    public String getEmail(){return email;}
}
