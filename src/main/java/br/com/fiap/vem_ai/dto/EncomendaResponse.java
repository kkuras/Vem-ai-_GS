package br.com.fiap.vem_ai.dto;

import br.com.fiap.vem_ai.enums.StatusEncomenda;

public record EncomendaResponse(
        int cdEncomenda,
        int cdCentro,
        int cdLote,
        int cdCliente,
        String codigoRastreio,
        Integer nrPosicaoRota,
        StatusEncomenda status
) { }
