package br.com.fiap.vem_ai.mapper;

import br.com.fiap.vem_ai.dto.CentroRequest;
import br.com.fiap.vem_ai.dto.CentroResponse;
import br.com.fiap.vem_ai.models.CentroDistribuicao;

public class CentroMapper {

    public static CentroDistribuicao toModel(CentroRequest request) {
        CentroDistribuicao centro = new CentroDistribuicao();
        centro.setNmCentro(request.nmCentro());
        centro.setDsEndereco(request.dsEndereco());
        centro.setVlLatitude(request.latitude());
        centro.setVlLongitude(request.longitude());
        return centro;
    }

    public static CentroResponse toResponse(CentroDistribuicao centro) {
        return new CentroResponse(centro.getCdCentro(), centro.getNmCentro(), centro.getDsEndereco(),
                centro.getVlLatitude(), centro.getVlLongitude());
    }
}
