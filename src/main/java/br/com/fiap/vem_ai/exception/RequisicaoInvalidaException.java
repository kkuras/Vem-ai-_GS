package br.com.fiap.vem_ai.exception;

public class RequisicaoInvalidaException extends ApiException {

    public RequisicaoInvalidaException(String mensagem) {
        super(400, "Requisição inválida", mensagem);
    }
}
