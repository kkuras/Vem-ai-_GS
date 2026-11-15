package br.com.fiap.vem_ai.dto;

import br.com.fiap.vem_ai.enums.StatusEntregador;
import jakarta.validation.constraints.NotNull;

public record DisponibilidadeRequest(@NotNull(message = "O status é obrigatório") StatusEntregador status) { }
