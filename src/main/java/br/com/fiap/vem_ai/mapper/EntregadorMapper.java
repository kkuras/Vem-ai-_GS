package br.com.fiap.vem_ai.mapper;

import br.com.fiap.vem_ai.dto.DisponibilidadeResponse;
import br.com.fiap.vem_ai.dto.EntregadorAtualizacaoRequest;
import br.com.fiap.vem_ai.dto.EntregadorRequest;
import br.com.fiap.vem_ai.dto.EntregadorResponse;
import br.com.fiap.vem_ai.models.Entregador;

import java.util.List;

public class EntregadorMapper {

    public static Entregador toModel(EntregadorRequest request) {
        Entregador entregador = new Entregador();
        entregador.setNmEntregador(request.nmEntregador());
        entregador.setDsEmail(request.dsEmail());
        entregador.setNrTelefone(request.nrTelefone());
        return entregador;
    }

    public static Entregador toModel(EntregadorAtualizacaoRequest request) {
        Entregador entregador = new Entregador();
        entregador.setNmEntregador(request.nmEntregador());
        entregador.setDsEmail(request.dsEmail());
        entregador.setNrTelefone(request.nrTelefone());
        return entregador;
    }

    public static EntregadorResponse toResponse(Entregador entregador, List<Integer> regioes) {
        return new EntregadorResponse(entregador.getCdEntregador(), entregador.getNmEntregador(),
                entregador.getDsEmail(), entregador.getNrTelefone(), entregador.getStatus(), regioes);
    }

    public static DisponibilidadeResponse toDisponibilidade(Entregador entregador) {
        return new DisponibilidadeResponse(entregador.getCdEntregador(), entregador.getStatus());
    }
}
