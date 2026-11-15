package br.com.fiap.vem_ai.dto;

import br.com.fiap.vem_ai.enums.TipoEvento;
import java.time.LocalDateTime;

public record EventoResponse(
        int cdEvento,
        int cdEncomenda,
        TipoEvento tipo,
        LocalDateTime dtEvento
) { }
