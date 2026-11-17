package br.com.fiap.vem_ai.exception;

public class RegraDeNegocioException extends ApiException {

    public RegraDeNegocioException(String mensagem) {
        super(409, "Conflito", mensagem);
    }
}
