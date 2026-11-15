package br.com.fiap.vem_ai.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CentroRequest(
        @NotBlank(message = "O nome do centro é obrigatório") @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres") String nmCentro,
        @NotBlank(message = "O endereço é obrigatório") @Size(max = 200, message = "O endereço deve ter no máximo 200 caracteres") String dsEndereco,
        @NotNull(message = "A latitude é obrigatória") @DecimalMin(value = "-90.0", message = "Latitude inválida") @DecimalMax(value = "90.0", message = "Latitude inválida") Double latitude,
        @NotNull(message = "A longitude é obrigatória") @DecimalMin(value = "-180.0", message = "Longitude inválida") @DecimalMax(value = "180.0", message = "Longitude inválida") Double longitude
) { }
