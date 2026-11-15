package br.com.fiap.vem_ai.dto;

import br.com.fiap.vem_ai.enums.TipoEvento;
import jakarta.validation.constraints.NotNull;

public record EventoRequest(@NotNull(message = "O tipo do evento é obrigatório") TipoEvento tipo) { }
