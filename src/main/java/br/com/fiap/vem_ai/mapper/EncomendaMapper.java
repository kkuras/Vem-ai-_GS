package br.com.fiap.vem_ai.mapper;

import br.com.fiap.vem_ai.dto.*;
import br.com.fiap.vem_ai.models.Encomenda;
import br.com.fiap.vem_ai.models.Entregador;

import java.util.List;

public class EncomendaMapper {

    public static Encomenda toModel(EncomendaRequest request) {
        Encomenda encomenda = new Encomenda();
        encomenda.setCdCentro(request.cdCentro());
        encomenda.setCdCliente(request.cdCliente());
        return encomenda;
    }

    public static Encomenda toModel(EncomendaAtualizacaoRequest request) {
        Encomenda encomenda = new Encomenda();
        encomenda.setCdCentro(request.cdCentro());
        encomenda.setCdLote(request.cdLote());
        encomenda.setCdCliente(request.cdCliente());
        encomenda.setNrPosicaoRota(request.nrPosicaoRota());
        encomenda.setStatus(request.status());
        return encomenda;
    }

    public static EncomendaResponse toResponse(Encomenda encomenda) {
        return new EncomendaResponse(encomenda.getCdEncomenda(), encomenda.getCdCentro(),
                encomenda.getCdLote(), encomenda.getCdCliente(), encomenda.getCodigoRastreio(),
                encomenda.getNrPosicaoRota(), encomenda.getStatus());
    }

    public static EncomendaBipadaResponse toBipada(Encomenda encomenda, Entregador entregador) {
        return new EncomendaBipadaResponse(encomenda.getCdEncomenda(), encomenda.getCodigoRastreio(),
                encomenda.getStatus(), encomenda.getCdLote(), entregador.getCdEntregador(),
                entregador.getNmEntregador());
    }

    public static ClienteEncomendaResponse toClienteEncomenda(Encomenda encomenda, Integer paradasRestantes) {
        return new ClienteEncomendaResponse(encomenda.getCdEncomenda(), encomenda.getCodigoRastreio(),
                encomenda.getStatus(), paradasRestantes);
    }

    public static EntregaResponse toEntrega(Encomenda encomenda, int paradasRestantesNoLote,
                                            List<Integer> avisados) {
        return new EntregaResponse(encomenda.getCdEncomenda(), encomenda.getStatus(),
                paradasRestantesNoLote, avisados);
    }
}
