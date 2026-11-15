package br.com.fiap.vem_ai.dto;

import br.com.fiap.vem_ai.enums.TipoUsuario;

public record LoginResponse(
        int id,
        TipoUsuario tipo,
        String nome
) { }
