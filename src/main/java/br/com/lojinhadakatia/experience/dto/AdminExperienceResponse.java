package br.com.lojinhadakatia.experience.dto;

import java.util.List;

/** Admin-only view. Never use on anonymous routes: includes voting keys and draft state. */
public record AdminExperienceResponse(
    Long id, String title, String description, boolean published,
    List<Question> questions, List<Result> results
) {
    public record Question(Long id, String title, int position, List<Option> options) {}
    public record Option(Long id, String label, int position, String resultKey) {}
    public record Result(Long id, String key, String title, String description, List<Long> productIds) {}
}
