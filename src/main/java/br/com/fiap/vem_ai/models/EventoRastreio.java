package br.com.fiap.vem_ai.models;

import br.com.fiap.vem_ai.enums.TipoEvento;

import java.time.LocalDateTime;

public class EventoRastreio {

    private int cdEvento;
    private int cdEncomenda;
    private TipoEvento tipo;
    private LocalDateTime dtEvento;

    public boolean ehAviso() {
        return true;
    }
}
