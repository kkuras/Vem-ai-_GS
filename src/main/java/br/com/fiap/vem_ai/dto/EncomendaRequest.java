package br.com.fiap.vem_ai.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record EncomendaRequest(
        @NotNull(message = "O centro é obrigatório") @Positive(message = "Centro inválido") Integer cdCentro,
        @NotNull(message = "O cliente é obrigatório") @Positive(message = "Cliente inválido") Integer cdCliente
) { }
