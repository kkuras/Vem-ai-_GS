package br.com.fiap.vem_ai.dto;

public record CentroResponse(
        int cdCentro,
        String nmCentro,
        String dsEndereco,
        double latitude,
        double longitude
) { }
