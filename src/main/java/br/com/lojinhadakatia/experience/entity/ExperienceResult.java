package br.com.lojinhadakatia.experience.entity;
import jakarta.persistence.*;
import java.util.*;
import br.com.lojinhadakatia.catalog.entity.Product;
/** Resultado definido pelo conteúdo comercial aprovado. */
@Entity @Table(name="experience_results")
public class ExperienceResult {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="experience_id") private Experience experience;
 @Column(name="result_key",nullable=false,length=80) private String resultKey;
 @Column(nullable=false,length=180) private String title;
 @Column(length=2000) private String description;
 @OneToMany(mappedBy="result", cascade=CascadeType.ALL, orphanRemoval=true)
 @OrderBy("position ASC, id ASC") private List<ExperienceResultRecommendation> recommendations=new ArrayList<>();
 protected ExperienceResult(){}
 public ExperienceResult(String key,String title,String description){resultKey=key;this.title=title;this.description=description;}
 void attach(Experience e){experience=e;}
 public Long getId(){return id;} public String getResultKey(){return resultKey;} public String getTitle(){return title;}
 public String getDescription(){return description;}
 public List<ExperienceResultRecommendation> getRecommendations(){return recommendations;}
 public void setRecommendations(List<Product> products){
  recommendations.clear();
  for(int i=0;i<products.size();i++)recommendations.add(new ExperienceResultRecommendation(this,products.get(i),i));
 }
}
