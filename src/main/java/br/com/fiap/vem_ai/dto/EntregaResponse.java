package br.com.fiap.vem_ai.dto;

import br.com.fiap.vem_ai.enums.StatusEncomenda;
import java.util.List;

public record EntregaResponse(
        int cdEncomenda,
        StatusEncomenda status,
        int paradasRestantesNoLote,
        List<Integer> avisados
) { }
