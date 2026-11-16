package br.com.fiap.vem_ai.mapper;

import br.com.fiap.vem_ai.dto.CoordenadasResponse;
import br.com.fiap.vem_ai.dto.RastreioResponse;
import br.com.fiap.vem_ai.models.Cliente;
import br.com.fiap.vem_ai.models.Encomenda;
import br.com.fiap.vem_ai.models.Lote;

public class RastreioMapper {

    public static RastreioResponse toResponse(Encomenda encomenda, Integer paradasRestantes,
                                              boolean exibirMapa, Lote lote, Cliente cliente) {
        CoordenadasResponse posicaoEntregador = null;
        if (exibirMapa && lote != null && lote.temPosicaoAtual()) {
            posicaoEntregador = new CoordenadasResponse(lote.getVlLatitudeAtual(), lote.getVlLongitudeAtual());
        }
        CoordenadasResponse destino = new CoordenadasResponse(cliente.getVlLatitude(), cliente.getVlLongitude());

        return new RastreioResponse(encomenda.getCodigoRastreio(), encomenda.getStatus(),
                paradasRestantes, exibirMapa, posicaoEntregador, destino);
    }
}
