package br.com.lojinhadakatia.experience.entity;
import jakarta.persistence.*;
/** Alternativa de escolha única associada a um resultado configurado pelo administrador. */
@Entity @Table(name="experience_options")
public class ExperienceOption {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="question_id") private ExperienceQuestion question;
 @Column(nullable=false,length=300) private String label;
 @Column(nullable=false) private int position;
 @Column(name="result_key",nullable=false,length=80) private String resultKey;
 protected ExperienceOption(){}
 public ExperienceOption(String label,int position,String resultKey){
  this.label=label;this.position=position;this.resultKey=resultKey;
 }
 void attach(ExperienceQuestion q){question=q;}
 public Long getId(){return id;} public String getLabel(){return label;} public int getPosition(){return position;}
 public String getResultKey(){return resultKey;}
}
