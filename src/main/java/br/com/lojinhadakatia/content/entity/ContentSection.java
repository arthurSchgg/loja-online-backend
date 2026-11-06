package br.com.lojinhadakatia.content.entity;
import jakarta.persistence.*;
import java.time.Instant;
/** Seção textual controlada, sem permitir HTML arbitrário no conteúdo. */
@Entity @Table(name="content_sections")
public class ContentSection {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true,length=80) private String slug;
 @Column(nullable=false,length=180) private String title;
 @Column(length=8000) private String body;
 @Column(name="image_url",length=2048) private String imageUrl;
 @Column(nullable=false) private int position;
 @Column(nullable=false) private boolean published;
 @Column(name="updated_at",nullable=false) private Instant updatedAt;
 protected ContentSection(){}
 public ContentSection(String slug,String title,String body,String imageUrl,int position,boolean published){
  this.slug=slug;update(title,body,imageUrl,position,published);
 }
 public void update(String title,String body,String imageUrl,int position,boolean published){
  this.title=title;this.body=body;this.imageUrl=imageUrl;this.position=position;this.published=published;updatedAt=Instant.now();
 }
 public Long getId(){return id;} public String getSlug(){return slug;} public String getTitle(){return title;}
 public String getBody(){return body;} public String getImageUrl(){return imageUrl;}
 public int getPosition(){return position;} public boolean isPublished(){return published;}
}
