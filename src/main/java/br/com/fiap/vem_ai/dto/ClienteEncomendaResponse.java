package br.com.fiap.vem_ai.dto;

import br.com.fiap.vem_ai.enums.StatusEncomenda;

public record ClienteEncomendaResponse(
        int cdEncomenda,
        String codigoRastreio,
        StatusEncomenda status,
        Integer paradasRestantes
) { }
