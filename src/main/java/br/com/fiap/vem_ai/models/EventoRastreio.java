package br.com.fiap.vem_ai.models;

import br.com.fiap.vem_ai.enums.TipoEvento;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventoRastreio {

    private int cdEvento;
    private int cdEncomenda;
    private TipoEvento tipo;
    private LocalDateTime dtEvento;

    public boolean ehAviso() {
        return tipo == TipoEvento.AVISO_ENVIADO;
    }
}
