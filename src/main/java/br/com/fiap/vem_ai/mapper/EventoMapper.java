package br.com.fiap.vem_ai.mapper;

import br.com.fiap.vem_ai.dto.EventoRequest;
import br.com.fiap.vem_ai.dto.EventoResponse;
import br.com.fiap.vem_ai.models.EventoRastreio;

public class EventoMapper {

    public static EventoRastreio toModel(EventoRequest request, int cdEncomenda) {
        EventoRastreio evento = new EventoRastreio();
        evento.setCdEncomenda(cdEncomenda);
        evento.setTipo(request.tipo());
        return evento;
    }

    public static EventoRastreio toModel(EventoRequest request) {
        EventoRastreio evento = new EventoRastreio();
        evento.setTipo(request.tipo());
        return evento;
    }

    public static EventoResponse toResponse(EventoRastreio evento) {
        return new EventoResponse(evento.getCdEvento(), evento.getCdEncomenda(),
                evento.getTipo(), evento.getDtEvento());
    }
}
