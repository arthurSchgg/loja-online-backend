package br.com.lojinhadakatia.shared.exception;

/** Violação de uma regra de domínio ou integridade de cadastro. */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) { super(message); }
}
