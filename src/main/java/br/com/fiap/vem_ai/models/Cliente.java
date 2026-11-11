package br.com.fiap.vem_ai.models;

import br.com.fiap.vem_ai.enums.TipoUsuario;

public class Cliente extends Usuario{

    private int cdCliente;
    private int cdRegiao;
    private String nmCliente;
    private String nrWhatsapp;
    private String dsEndereco;
    private double vlLatitude;
    private double vlLongitude;

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
        return null;
    }
}
