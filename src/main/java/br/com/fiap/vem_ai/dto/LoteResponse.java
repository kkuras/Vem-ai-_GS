package br.com.fiap.vem_ai.dto;

import br.com.fiap.vem_ai.enums.StatusLote;
import java.time.LocalDateTime;

public record LoteResponse(
        int cdLote,
        int cdCentro,
        int cdEntregador,
        String codigoQr,
        StatusLote status,
        LocalDateTime dtCriacao,
        LocalDateTime dtInicioRota,
        Double latitudeAtual,
        Double longitudeAtual
) { }
