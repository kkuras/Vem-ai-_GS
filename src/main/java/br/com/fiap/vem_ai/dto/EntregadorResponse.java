package br.com.fiap.vem_ai.dto;

import br.com.fiap.vem_ai.enums.StatusEntregador;
import java.util.List;

public record EntregadorResponse(
        int cdEntregador,
        String nmEntregador,
        String dsEmail,
        String nrTelefone,
        StatusEntregador status,
        List<Integer> regioes
) { }
