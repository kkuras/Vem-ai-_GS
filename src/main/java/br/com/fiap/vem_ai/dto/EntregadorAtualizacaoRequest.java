package br.com.fiap.vem_ai.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EntregadorAtualizacaoRequest(
        @NotBlank(message = "O nome é obrigatório") @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres") String nmEntregador,
        @NotBlank(message = "O e-mail é obrigatório") @Email(message = "E-mail inválido") @Size(max = 120, message = "O e-mail deve ter no máximo 120 caracteres") String dsEmail,
        @NotBlank(message = "O telefone é obrigatório") @Size(max = 20, message = "O telefone deve ter no máximo 20 caracteres") String nrTelefone
) { }
