package br.com.fiap.vem_ai.dto;

import br.com.fiap.vem_ai.enums.StatusLote;
import java.util.List;

public record IniciarRotaResponse(
        int cdLote,
        StatusLote status,
        List<ParadaResponse> encomendas
) { }
