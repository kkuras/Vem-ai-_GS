package br.com.fiap.vem_ai.dto;

import java.util.Map;

public record ErroResponse(
        int status,
        String erro,
        String mensagem,
        Map<String, String> campos
) { }
