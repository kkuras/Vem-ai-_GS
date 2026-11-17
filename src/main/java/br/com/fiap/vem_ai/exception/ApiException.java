package br.com.fiap.vem_ai.exception;

public abstract class ApiException extends RuntimeException {

    private final int status;
    private final String erro;

    protected ApiException(int status, String erro, String mensagem) {
        super(mensagem);
        this.status = status;
        this.erro = erro;
    }

    public int getStatus() {
        return status;
    }

    public String getErro() {
        return erro;
    }
}
