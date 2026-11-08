package br.com.lojinhadakatia.offering.entity;

import jakarta.persistence.*;

@Entity @Table(name="offering_options")
public class OfferingOption {
 public enum Kind { AROMA, COLOR, PACKAGING }
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="offering_id",nullable=false)
 private ServiceOffering offering;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=16) private Kind kind;
 @Column(name="option_value",nullable=false,length=120) private String value;
 @Column(nullable=false) private boolean active=true;
 protected OfferingOption(){}
 public OfferingOption(Kind kind,String value,boolean active){
  this.kind=kind;this.value=value;this.active=active;
 }
 void assign(ServiceOffering o){this.offering=o;}
 public Long getId(){return id;} public Kind getKind(){return kind;}
 public String getValue(){return value;} public boolean isActive(){return active;}
}
