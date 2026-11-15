package br.com.fiap.vem_ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record IniciarRotaRequest(
        @NotBlank(message = "O código do QR é obrigatório") String codigoQr,
        @NotNull(message = "O entregador é obrigatório") @Positive(message = "Entregador inválido") Integer cdEntregador
) { }
