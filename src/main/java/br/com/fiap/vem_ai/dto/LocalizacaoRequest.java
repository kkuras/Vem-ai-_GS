package br.com.fiap.vem_ai.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record LocalizacaoRequest(
        @NotNull(message = "A latitude é obrigatória") @DecimalMin(value = "-90.0", message = "Latitude inválida") @DecimalMax(value = "90.0", message = "Latitude inválida") Double latitude,
        @NotNull(message = "A longitude é obrigatória") @DecimalMin(value = "-180.0", message = "Longitude inválida") @DecimalMax(value = "180.0", message = "Longitude inválida") Double longitude
) { }
