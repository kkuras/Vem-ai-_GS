package br.com.fiap.vem_ai.mapper;

import br.com.fiap.vem_ai.dto.ClienteParadaResponse;
import br.com.fiap.vem_ai.dto.IniciarRotaResponse;
import br.com.fiap.vem_ai.dto.ParadaResponse;
import br.com.fiap.vem_ai.models.Cliente;
import br.com.fiap.vem_ai.models.Encomenda;
import br.com.fiap.vem_ai.models.Lote;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RotaMapper {

    public static ParadaResponse toParada(Encomenda encomenda, Cliente cliente) {
        ClienteParadaResponse destino = new ClienteParadaResponse(cliente.getNmCliente(),
                cliente.getDsEndereco(), cliente.getVlLatitude(), cliente.getVlLongitude());
        return new ParadaResponse(encomenda.getCdEncomenda(), encomenda.getNrPosicaoRota(), destino);
    }

    public static IniciarRotaResponse toResponse(Lote lote, List<Encomenda> encomendas,
                                                 Map<Integer, Cliente> clientesPorCodigo) {
        List<ParadaResponse> paradas = new ArrayList<>();
        for (Encomenda encomenda : encomendas) {
            paradas.add(toParada(encomenda, clientesPorCodigo.get(encomenda.getCdCliente())));
        }
        return new IniciarRotaResponse(lote.getCdLote(), lote.getStatus(), paradas);
    }
}
