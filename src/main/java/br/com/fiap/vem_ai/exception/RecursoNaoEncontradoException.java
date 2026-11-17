package br.com.fiap.vem_ai.exception;

public class RecursoNaoEncontradoException extends ApiException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(404, "Não encontrado", mensagem);
    }
}
