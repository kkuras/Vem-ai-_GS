package br.com.fiap.vem_ai.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ClienteRequest(
        @NotBlank(message = "O nome é obrigatório") @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres") String nmCliente,
        @NotBlank(message = "O e-mail é obrigatório") @Email(message = "E-mail inválido") @Size(max = 120, message = "O e-mail deve ter no máximo 120 caracteres") String dsEmail,
        @NotBlank(message = "A senha é obrigatória") @Size(min = 6, max = 72, message = "A senha deve ter entre 6 e 72 caracteres") String senha,
        @NotBlank(message = "O WhatsApp é obrigatório") @Size(max = 20, message = "O WhatsApp deve ter no máximo 20 caracteres") String nrWhatsapp,
        @NotBlank(message = "O endereço é obrigatório") @Size(max = 200, message = "O endereço deve ter no máximo 200 caracteres") String dsEndereco,
        @NotNull(message = "A latitude é obrigatória") @DecimalMin(value = "-90.0", message = "Latitude inválida") @DecimalMax(value = "90.0", message = "Latitude inválida") Double latitude,
        @NotNull(message = "A longitude é obrigatória") @DecimalMin(value = "-180.0", message = "Longitude inválida") @DecimalMax(value = "180.0", message = "Longitude inválida") Double longitude,
        @NotNull(message = "A região é obrigatória") @Positive(message = "Região inválida") Integer cdRegiao
) { }
