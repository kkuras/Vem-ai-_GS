package br.com.fiap.vem_ai.mapper;

import br.com.fiap.vem_ai.dto.LoteRequest;
import br.com.fiap.vem_ai.dto.LoteResponse;
import br.com.fiap.vem_ai.dto.LoteResumoResponse;
import br.com.fiap.vem_ai.models.Lote;

public class LoteMapper {

    public static Lote toModel(LoteRequest request) {
        Lote lote = new Lote();
        lote.setCdCentro(request.cdCentro());
        lote.setCdEntregador(request.cdEntregador());
        lote.setStatus(request.status());
        return lote;
    }

    public static LoteResponse toResponse(Lote lote) {
        return new LoteResponse(lote.getCdLote(), lote.getCdCentro(), lote.getCdEntregador(),
                lote.getCodigoQr(), lote.getStatus(), lote.getDtCriacao(), lote.getDtInicioRota(),
                lote.getVlLatitudeAtual(), lote.getVlLongitudeAtual());
    }

    public static LoteResumoResponse toResumo(Lote lote, String nmEntregador, int qtdEncomendas) {
        return new LoteResumoResponse(lote.getCdLote(), lote.getCdEntregador(), nmEntregador,
                qtdEncomendas, lote.getCodigoQr());
    }
}
