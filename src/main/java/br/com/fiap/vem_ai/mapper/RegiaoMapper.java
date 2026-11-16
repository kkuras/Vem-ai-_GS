package br.com.fiap.vem_ai.mapper;

import br.com.fiap.vem_ai.dto.RegiaoRequest;
import br.com.fiap.vem_ai.dto.RegiaoResponse;
import br.com.fiap.vem_ai.models.Regiao;

public class RegiaoMapper {

    public static Regiao toModel(RegiaoRequest request) {
        Regiao regiao = new Regiao();
        regiao.setNmRegiao(request.nmRegiao());
        return regiao;
    }

    public static RegiaoResponse toResponse(Regiao regiao) {
        return new RegiaoResponse(regiao.getCdRegiao(), regiao.getNmRegiao());
    }
}
