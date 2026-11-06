package br.com.lojinhadakatia.shared.exception;

import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.validation.ConstraintViolationException;

/** Centraliza erros sem expor stack traces ou detalhes de infraestrutura. */
@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<ProblemDetail> notFound(ResourceNotFoundException exception) {
        return problem(HttpStatus.NOT_FOUND, "Recurso não encontrado", exception.getMessage());
    }
    @ExceptionHandler(BusinessException.class)
    ResponseEntity<ProblemDetail> conflict(BusinessException exception) {
        return problem(HttpStatus.CONFLICT, "Regra de negócio", exception.getMessage());
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ProblemDetail> validation(MethodArgumentNotValidException exception) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Confira os campos enviados.");
        detail.setTitle("Dados inválidos");
        detail.setProperty("errors", exception.getBindingResult().getFieldErrors().stream().map(e ->
            java.util.Map.of("field", e.getField(), "message", e.getDefaultMessage() == null ? "Inválido" : e.getDefaultMessage())).toList());
        return ResponseEntity.badRequest().body(detail);
    }
    @ExceptionHandler({HttpMessageNotReadableException.class, ConstraintViolationException.class, IllegalArgumentException.class})
    ResponseEntity<ProblemDetail> invalid(Exception exception) {
        return problem(HttpStatus.BAD_REQUEST, "Requisição inválida", "Verifique o formato e os valores informados.");
    }
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    ResponseEntity<ProblemDetail> integrity(Exception exception) {
        return problem(HttpStatus.CONFLICT, "Conflito de dados", "O cadastro conflita com outro registro ou vínculo existente.");
    }
    private ResponseEntity<ProblemDetail> problem(HttpStatus status, String title, String message) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(status, message);
        detail.setTitle(title);
        detail.setType(URI.create("about:blank"));
        return ResponseEntity.status(status).body(detail);
    }
}
