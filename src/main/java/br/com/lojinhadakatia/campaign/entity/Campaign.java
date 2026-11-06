package br.com.lojinhadakatia.campaign.entity;
import jakarta.persistence.*;
import java.time.Instant;
/** Banner de destaque; não concede descontos nem altera o preço de produtos. */
@Entity @Table(name="campaigns")
public class Campaign {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,length=180) private String title;
 @Column(length=600) private String subtitle;
 @Column(name="image_url",length=2048) private String imageUrl;
 @Column(name="link_url",length=2048) private String linkUrl;
 @Column(nullable=false) private int position;
 @Column(nullable=false) private boolean published;
 @Column(name="starts_at") private Instant startsAt;
 @Column(name="ends_at") private Instant endsAt;
 protected Campaign(){}
 public Campaign(String title,String subtitle,String imageUrl,String linkUrl,int position,
                 boolean published,Instant startsAt,Instant endsAt){
  update(title,subtitle,imageUrl,linkUrl,position,published,startsAt,endsAt);
 }
 public void update(String title,String subtitle,String imageUrl,String linkUrl,int position,
                    boolean published,Instant startsAt,Instant endsAt){
  this.title=title;this.subtitle=subtitle;this.imageUrl=imageUrl;this.linkUrl=linkUrl;
  this.position=position;this.published=published;this.startsAt=startsAt;this.endsAt=endsAt;
 }
 public boolean isVisibleAt(Instant instant){
  return published&&(startsAt==null||!startsAt.isAfter(instant))&&(endsAt==null||endsAt.isAfter(instant));
 }
 public Long getId(){return id;} public String getTitle(){return title;} public String getSubtitle(){return subtitle;}
 public String getImageUrl(){return imageUrl;} public String getLinkUrl(){return linkUrl;}
 public int getPosition(){return position;} public boolean isPublished(){return published;}
 public Instant getStartsAt(){return startsAt;} public Instant getEndsAt(){return endsAt;}
}
