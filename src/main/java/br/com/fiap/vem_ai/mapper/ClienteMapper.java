package br.com.fiap.vem_ai.mapper;

import br.com.fiap.vem_ai.dto.ClienteAtualizacaoRequest;
import br.com.fiap.vem_ai.dto.ClienteRequest;
import br.com.fiap.vem_ai.dto.ClienteResponse;
import br.com.fiap.vem_ai.models.Cliente;

public class ClienteMapper {

    public static Cliente toModel(ClienteRequest request) {
        Cliente cliente = new Cliente();
        cliente.setNmCliente(request.nmCliente());
        cliente.setDsEmail(request.dsEmail());
        cliente.setNrWhatsapp(request.nrWhatsapp());
        cliente.setDsEndereco(request.dsEndereco());
        cliente.setVlLatitude(request.latitude());
        cliente.setVlLongitude(request.longitude());
        cliente.setCdRegiao(request.cdRegiao());
        return cliente;
    }

    public static Cliente toModel(ClienteAtualizacaoRequest request) {
        Cliente cliente = new Cliente();
        cliente.setNmCliente(request.nmCliente());
        cliente.setDsEmail(request.dsEmail());
        cliente.setNrWhatsapp(request.nrWhatsapp());
        cliente.setDsEndereco(request.dsEndereco());
        cliente.setVlLatitude(request.latitude());
        cliente.setVlLongitude(request.longitude());
        cliente.setCdRegiao(request.cdRegiao());
        return cliente;
    }

    public static ClienteResponse toResponse(Cliente cliente) {
        return new ClienteResponse(cliente.getCdCliente(), cliente.getNmCliente(), cliente.getDsEmail(),
                cliente.getNrWhatsapp(), cliente.getDsEndereco(), cliente.getVlLatitude(),
                cliente.getVlLongitude(), cliente.getCdRegiao());
    }
}
