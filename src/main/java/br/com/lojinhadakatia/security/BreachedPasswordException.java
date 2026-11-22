package br.com.lojinhadakatia.security;

/** Raised when a candidate password is known to appear in public breach corpora. */
public class BreachedPasswordException extends RuntimeException {
    public BreachedPasswordException() {
        super("Esta senha consta em vazamentos conhecidos. Escolha outra senha.");
    }
}
