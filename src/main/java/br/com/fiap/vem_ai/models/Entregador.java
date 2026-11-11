package br.com.fiap.vem_ai.models;

import br.com.fiap.vem_ai.enums.StatusEntregador;
import br.com.fiap.vem_ai.enums.TipoUsuario;

public class Entregador extends Usuario{

    private int cdEntregador;
    private String nmEntregador;
    private String nrTelefone;
    private StatusEntregador status;

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public String getNome() {
        return "";
    }

    @Override
    public TipoUsuario getTipo() {
        return TipoUsuario.ENTREGADOR;
    }

    public boolean estaDisponivel() {
        return status == StatusEntregador.DISPONIVEL;
    }
}
