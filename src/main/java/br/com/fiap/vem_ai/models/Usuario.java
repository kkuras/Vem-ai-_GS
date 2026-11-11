package br.com.fiap.vem_ai.models;

import br.com.fiap.vem_ai.enums.TipoUsuario;

public abstract class Usuario {

    private String dsEmail;
    private String dsSenhaHah;

    public abstract int getId();
    public abstract String getNome();
    public abstract TipoUsuario getTipo();

}
