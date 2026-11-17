package br.com.fiap.vem_ai.service;

import br.com.fiap.vem_ai.dao.ClienteDao;
import br.com.fiap.vem_ai.dao.RegiaoDao;
import br.com.fiap.vem_ai.dto.ClienteAtualizacaoRequest;
import br.com.fiap.vem_ai.dto.ClienteRequest;
import br.com.fiap.vem_ai.dto.ClienteResponse;
import br.com.fiap.vem_ai.exception.RecursoNaoEncontradoException;
import br.com.fiap.vem_ai.exception.RegraDeNegocioException;
import br.com.fiap.vem_ai.mapper.ClienteMapper;
import br.com.fiap.vem_ai.models.Cliente;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteDao clienteDao;
    private final RegiaoDao regiaoDao;
    private final SenhaService senhaService;

    public ClienteService(ClienteDao clienteDao, RegiaoDao regiaoDao, SenhaService senhaService) {
        this.clienteDao = clienteDao;
        this.regiaoDao = regiaoDao;
        this.senhaService = senhaService;
    }

    public ClienteResponse cadastrar(ClienteRequest request) throws SQLException {
        if (clienteDao.buscarPorEmail(request.dsEmail()).isPresent()) {
            throw new RegraDeNegocioException("E-mail já cadastrado");
        }
        exigirRegiao(request.cdRegiao());

        Cliente cliente = ClienteMapper.toModel(request);
        cliente.setDsSenhaHash(senhaService.gerarHash(request.senha()));
        return ClienteMapper.toResponse(clienteDao.inserir(cliente));
    }

    public List<ClienteResponse> listar() throws SQLException {
        List<ClienteResponse> respostas = new ArrayList<>();
        for (Cliente cliente : clienteDao.listar()) {
            respostas.add(ClienteMapper.toResponse(cliente));
        }
        return respostas;
    }

    public ClienteResponse buscarPorId(int cdCliente) throws SQLException {
        return ClienteMapper.toResponse(buscarOuFalhar(cdCliente));
    }

    public ClienteResponse atualizar(int cdCliente, ClienteAtualizacaoRequest request) throws SQLException {
        buscarOuFalhar(cdCliente);
        exigirRegiao(request.cdRegiao());

        Optional<Cliente> comMesmoEmail = clienteDao.buscarPorEmail(request.dsEmail());
        if (comMesmoEmail.isPresent() && comMesmoEmail.get().getCdCliente() != cdCliente) {
            throw new RegraDeNegocioException("E-mail já cadastrado");
        }

        Cliente cliente = ClienteMapper.toModel(request);
        cliente.setCdCliente(cdCliente);
        clienteDao.atualizar(cliente);
        return ClienteMapper.toResponse(cliente);
    }

    public void remover(int cdCliente) throws SQLException {
        if (!clienteDao.remover(cdCliente)) {
            throw new RecursoNaoEncontradoException("Cliente não encontrado");
        }
    }

    private Cliente buscarOuFalhar(int cdCliente) throws SQLException {
        Optional<Cliente> cliente = clienteDao.buscarPorId(cdCliente);
        if (cliente.isEmpty()) {
            throw new RecursoNaoEncontradoException("Cliente não encontrado");
        }
        return cliente.get();
    }

    private void exigirRegiao(int cdRegiao) throws SQLException {
        if (regiaoDao.buscarPorId(cdRegiao).isEmpty()) {
            throw new RecursoNaoEncontradoException("Região não encontrada");
        }
    }
}
