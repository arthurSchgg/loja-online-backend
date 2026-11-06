package br.com.lojinhadakatia.experience.entity;
import jakarta.persistence.*;
import java.util.*;
/** Pergunta da experiência; alternativas possuem chave de resultado. */
@Entity @Table(name="experience_questions")
public class ExperienceQuestion {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="experience_id") private Experience experience;
 @Column(nullable=false,length=350) private String title;
 @Column(nullable=false) private int position;
 @OneToMany(mappedBy="question",cascade=CascadeType.ALL,orphanRemoval=true) @OrderBy("position ASC,id ASC")
 private List<ExperienceOption> options=new ArrayList<>();
 protected ExperienceQuestion(){}
 public ExperienceQuestion(String title,int position,List<ExperienceOption> options){
  this.title=title;this.position=position;options.forEach(o->{o.attach(this);this.options.add(o);});
 }
 void attach(Experience experience){this.experience=experience;}
 public Long getId(){return id;} public String getTitle(){return title;} public int getPosition(){return position;}
 public List<ExperienceOption> getOptions(){return options;}
}
