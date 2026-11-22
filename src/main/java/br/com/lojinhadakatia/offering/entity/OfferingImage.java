package br.com.lojinhadakatia.offering.entity;

import jakarta.persistence.*;

@Entity
@Table(name="offering_images")
public class OfferingImage {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="offering_id") private ServiceOffering offering;
    @Column(name="image_url",nullable=false,length=2048) private String url;
    @Column(nullable=false) private int position;
    @Column(name="alt_text",length=180) private String altText;
    protected OfferingImage() {}
    public OfferingImage(ServiceOffering offering,String url,int position,String altText){
        this.offering=offering;this.url=url;this.position=position;this.altText=altText;
    }
    public String getUrl(){return url;}
    public int getPosition(){return position;}
    public String getAltText(){return altText;}
}
