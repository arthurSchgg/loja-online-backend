package br.com.lojinhadakatia.experience.dto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;
/** Payload administrativo com perguntas, alternativas e resultados configuráveis. */
public record ExperienceRequest(
 @NotBlank @Size(max=180) String title,
 @Size(max=2000) String description,
 boolean published,
 @NotNull List<@Valid Question> questions,
 @NotNull List<@Valid Result> results
) {
 public record Question(@NotBlank @Size(max=350) String title,@Min(0) int position,
                        @NotNull List<@Valid Option> options) {}
 public record Option(@NotBlank @Size(max=300) String label,@Min(0) int position,
                      @NotBlank @Size(max=80) String resultKey) {}
 public record Result(@NotBlank @Size(max=80) String key,@NotBlank @Size(max=180) String title,
                      @Size(max=2000) String description) {}
}
