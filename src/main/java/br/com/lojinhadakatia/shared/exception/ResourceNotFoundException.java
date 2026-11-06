package br.com.lojinhadakatia.shared.exception;

/** Recurso inexistente ou não disponibilizado para consulta pública. */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) { super(message); }
}
