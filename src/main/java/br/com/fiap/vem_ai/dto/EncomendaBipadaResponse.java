package br.com.fiap.vem_ai.dto;

import br.com.fiap.vem_ai.enums.StatusEncomenda;

public record EncomendaBipadaResponse(
        int cdEncomenda,
        String codigoRastreio,
        StatusEncomenda status,
        int cdLote,
        int cdEntregador,
        String nmEntregador
) { }
