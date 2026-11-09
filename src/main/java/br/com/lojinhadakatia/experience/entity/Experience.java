package br.com.lojinhadakatia.experience.entity;
import jakarta.persistence.*;
import java.util.*;
/** Agregado de uma experiência interativa orientada por resultados. */
@Entity @Table(name="experiences")
public class Experience {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,length=180) private String title;
 @Column(length=2000) private String description;
 @Column(nullable=false) private boolean published;
 @OneToMany(mappedBy="experience",cascade=CascadeType.ALL,orphanRemoval=true) @OrderBy("position ASC,id ASC")
 private List<ExperienceQuestion> questions=new ArrayList<>();
 @OneToMany(mappedBy="experience",cascade=CascadeType.ALL,orphanRemoval=true)
 private List<ExperienceResult> results=new ArrayList<>();
 protected Experience(){}
 public Experience(String title,String description){this.title=title;this.description=description;}
 public void replace(String title,String description,boolean published,List<ExperienceQuestion> questions,List<ExperienceResult> results){
  this.title=title;this.description=description;this.published=published;
  this.questions.clear();this.results.clear();
  questions.forEach(q->{q.attach(this);this.questions.add(q);});
  results.forEach(r->{r.attach(this);this.results.add(r);});
 }
 public Long getId(){return id;} public String getTitle(){return title;} public String getDescription(){return description;}
 public boolean isPublished(){return published;} public List<ExperienceQuestion> getQuestions(){return questions;}
 public List<ExperienceResult> getResults(){return results;}
}
