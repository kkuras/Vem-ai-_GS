package br.com.fiap.vem_ai.dto;

import br.com.fiap.vem_ai.enums.StatusLote;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record LoteRequest(
        @NotNull(message = "O centro é obrigatório") @Positive(message = "Centro inválido") Integer cdCentro,
        @NotNull(message = "O entregador é obrigatório") @Positive(message = "Entregador inválido") Integer cdEntregador,
        StatusLote status
) { }
