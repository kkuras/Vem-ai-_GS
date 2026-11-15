package br.com.fiap.vem_ai.dto;

public record ClienteResponse(
        int cdCliente,
        String nmCliente,
        String dsEmail,
        String nrWhatsapp,
        String dsEndereco,
        double latitude,
        double longitude,
        int cdRegiao
) { }
