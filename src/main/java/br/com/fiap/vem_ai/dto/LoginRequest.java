package br.com.fiap.vem_ai.dto;

import br.com.fiap.vem_ai.enums.TipoUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(
        @NotNull(message = "O tipo é obrigatório") TipoUsuario tipo,
        @NotBlank(message = "O e-mail é obrigatório") @Email(message = "E-mail inválido") String dsEmail,
        @NotBlank(message = "A senha é obrigatória") String senha
) { }
