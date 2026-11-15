package br.com.fiap.vem_ai.dto;

import br.com.fiap.vem_ai.enums.StatusEncomenda;

public record RastreioResponse(
        String codigoRastreio,
        StatusEncomenda status,
        Integer paradasRestantes,
        boolean exibirMapa,
        CoordenadasResponse posicaoEntregador,
        CoordenadasResponse destino
) { }
