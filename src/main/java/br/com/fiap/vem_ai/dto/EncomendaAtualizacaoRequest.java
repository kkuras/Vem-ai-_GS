package br.com.fiap.vem_ai.dto;

import br.com.fiap.vem_ai.enums.StatusEncomenda;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record EncomendaAtualizacaoRequest(
        @NotNull(message = "O centro é obrigatório") @Positive(message = "Centro inválido") Integer cdCentro,
        @NotNull(message = "O lote é obrigatório") @Positive(message = "Lote inválido") Integer cdLote,
        @NotNull(message = "O cliente é obrigatório") @Positive(message = "Cliente inválido") Integer cdCliente,
        @Positive(message = "Posição inválida") Integer nrPosicaoRota,
        @NotNull(message = "O status é obrigatório") StatusEncomenda status
) { }
