package br.com.fiap.vem_ai.exception;

public class AcessoNegadoException extends ApiException {

    public AcessoNegadoException(String mensagem) {
        super(403, "Acesso negado", mensagem);
    }
}
