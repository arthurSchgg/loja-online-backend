package br.com.lojinhadakatia.experience.dto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

/** Respostas anônimas; validação em cascata e limite de requisição. */
public record EvaluationRequest(
    @NotEmpty @Size(max = 30) List<@NotNull @Valid Answer> answers
) {
    public record Answer(
        @NotNull @Positive Long questionId,
        @NotNull @Positive Long optionId
    ) {}
}
