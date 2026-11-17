package br.com.fiap.vem_ai.exception;

public class CredenciaisInvalidasException extends ApiException {

    public CredenciaisInvalidasException() {
        super(401, "Não autorizado", "E-mail ou senha incorretos");
    }
}
