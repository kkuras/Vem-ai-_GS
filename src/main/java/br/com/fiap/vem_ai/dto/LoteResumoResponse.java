package br.com.fiap.vem_ai.dto;

public record LoteResumoResponse(
        int cdLote,
        int cdEntregador,
        String nmEntregador,
        int qtdEncomendas,
        String codigoQr
) { }
