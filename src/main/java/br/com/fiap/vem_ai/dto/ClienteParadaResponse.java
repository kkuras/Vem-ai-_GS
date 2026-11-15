package br.com.fiap.vem_ai.dto;

public record ClienteParadaResponse(
        String nmCliente,
        String dsEndereco,
        double latitude,
        double longitude
) { }
