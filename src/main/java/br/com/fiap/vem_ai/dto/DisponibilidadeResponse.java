package br.com.fiap.vem_ai.dto;

import br.com.fiap.vem_ai.enums.StatusEntregador;

public record DisponibilidadeResponse(
        int cdEntregador,
        StatusEntregador status
) { }
