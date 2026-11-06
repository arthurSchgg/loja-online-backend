package br.com.lojinhadakatia.experience.dto;
import java.util.List;
/** Estrutura pública: não revela as chaves que determinam o resultado. */
public record ExperienceResponse(Long id,String title,String description,List<Question> questions) {
 public record Question(Long id,String title,int position,List<Option> options) {}
 public record Option(Long id,String label,int position) {}
}
