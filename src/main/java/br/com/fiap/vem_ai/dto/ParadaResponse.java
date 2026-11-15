package br.com.fiap.vem_ai.dto;

public record ParadaResponse(
        int cdEncomenda,
        Integer nrPosicaoRota,
        ClienteParadaResponse cliente
) { }
