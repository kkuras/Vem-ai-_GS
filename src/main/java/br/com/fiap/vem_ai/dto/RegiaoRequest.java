package br.com.fiap.vem_ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegiaoRequest(
        @NotBlank(message = "O nome da região é obrigatório") @Size(max = 80, message = "O nome deve ter no máximo 80 caracteres") String nmRegiao) { }
