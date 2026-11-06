package br.com.lojinhadakatia.experience.dto;
import jakarta.validation.constraints.*;
import java.util.List;
/** Resposta anônima sem persistência, nome, e-mail ou dados sensíveis. */
public record EvaluationRequest(@NotEmpty List<@NotNull Answer> answers) {
 public record Answer(@NotNull Long questionId,@NotNull Long optionId) {}
}
