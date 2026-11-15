package br.com.fiap.vem_ai.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record EntregadorRequest(
        @NotBlank(message = "O nome é obrigatório") @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres") String nmEntregador,
        @NotBlank(message = "O e-mail é obrigatório") @Email(message = "E-mail inválido") @Size(max = 120, message = "O e-mail deve ter no máximo 120 caracteres") String dsEmail,
        @NotBlank(message = "A senha é obrigatória") @Size(min = 6, max = 72, message = "A senha deve ter entre 6 e 72 caracteres") String senha,
        @NotBlank(message = "O telefone é obrigatório") @Size(max = 20, message = "O telefone deve ter no máximo 20 caracteres") String nrTelefone,
        @NotEmpty(message = "Informe ao menos uma região") List<@NotNull(message = "Região inválida") Integer> regioes
) { }
