package br.com.fiap.vem_ai.service;

import br.com.fiap.vem_ai.dao.ConnectionFactory;
import br.com.fiap.vem_ai.dao.EntregadorDao;
import br.com.fiap.vem_ai.dao.LoteDao;
import br.com.fiap.vem_ai.dao.RegiaoDao;
import br.com.fiap.vem_ai.dto.DisponibilidadeRequest;
import br.com.fiap.vem_ai.dto.DisponibilidadeResponse;
import br.com.fiap.vem_ai.dto.EntregadorAtualizacaoRequest;
import br.com.fiap.vem_ai.dto.EntregadorRequest;
import br.com.fiap.vem_ai.dto.EntregadorResponse;
import br.com.fiap.vem_ai.enums.StatusEntregador;
import br.com.fiap.vem_ai.exception.RecursoNaoEncontradoException;
import br.com.fiap.vem_ai.exception.RegraDeNegocioException;
import br.com.fiap.vem_ai.mapper.EntregadorMapper;
import br.com.fiap.vem_ai.models.Entregador;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class EntregadorService {

    private final EntregadorDao entregadorDao;
    private final RegiaoDao regiaoDao;
    private final LoteDao loteDao;
    private final SenhaService senhaService;

    public EntregadorService(EntregadorDao entregadorDao, RegiaoDao regiaoDao,
                             LoteDao loteDao, SenhaService senhaService) {
        this.entregadorDao = entregadorDao;
        this.regiaoDao = regiaoDao;
        this.loteDao = loteDao;
        this.senhaService = senhaService;
    }

    public EntregadorResponse cadastrar(EntregadorRequest request) throws SQLException {
        if (entregadorDao.buscarPorEmail(request.dsEmail()).isPresent()) {
            throw new RegraDeNegocioException("E-mail já cadastrado");
        }

        Set<Integer> regioes = new LinkedHashSet<>(request.regioes());
        for (int cdRegiao : regioes) {
            if (regiaoDao.buscarPorId(cdRegiao).isEmpty()) {
                throw new RecursoNaoEncontradoException("Região " + cdRegiao + " não encontrada");
            }
        }

        Entregador entregador = EntregadorMapper.toModel(request);
        entregador.setDsSenhaHash(senhaService.gerarHash(request.senha()));
        entregador.setStatus(StatusEntregador.INDISPONIVEL);

        try (Connection con = ConnectionFactory.obterconexao()) {
            try {
                con.setAutoCommit(false);
                entregadorDao.inserir(con, entregador);
                for (int cdRegiao : regioes) {
                    entregadorDao.vincularRegiao(con, entregador.getCdEntregador(), cdRegiao);
                }
                con.commit();
            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            }
        }
        return EntregadorMapper.toResponse(entregador, new ArrayList<>(regioes));
    }

    public List<EntregadorResponse> listar() throws SQLException {
        List<EntregadorResponse> respostas = new ArrayList<>();
        for (Entregador entregador : entregadorDao.listar()) {
            List<Integer> regioes = entregadorDao.listarCodigosRegioes(entregador.getCdEntregador());
            respostas.add(EntregadorMapper.toResponse(entregador, regioes));
        }
        return respostas;
    }

    public EntregadorResponse buscarPorId(int cdEntregador) throws SQLException {
        Entregador entregador = buscarOuFalhar(cdEntregador);
        return EntregadorMapper.toResponse(entregador, entregadorDao.listarCodigosRegioes(cdEntregador));
    }

    public EntregadorResponse atualizar(int cdEntregador, EntregadorAtualizacaoRequest request)
            throws SQLException {
        buscarOuFalhar(cdEntregador);

        Optional<Entregador> comMesmoEmail = entregadorDao.buscarPorEmail(request.dsEmail());
        if (comMesmoEmail.isPresent() && comMesmoEmail.get().getCdEntregador() != cdEntregador) {
            throw new RegraDeNegocioException("E-mail já cadastrado");
        }

        Entregador entregador = EntregadorMapper.toModel(request);
        entregador.setCdEntregador(cdEntregador);
        entregadorDao.atualizar(entregador);
        return buscarPorId(cdEntregador);
    }

    public void remover(int cdEntregador) throws SQLException {
        if (!entregadorDao.remover(cdEntregador)) {
            throw new RecursoNaoEncontradoException("Entregador não encontrado");
        }
    }

    public DisponibilidadeResponse alterarDisponibilidade(int cdEntregador, DisponibilidadeRequest request)
            throws SQLException {
        Entregador entregador = buscarOuFalhar(cdEntregador);

        if (request.status() == StatusEntregador.INDISPONIVEL
                && loteDao.buscarEmRotaDoEntregador(cdEntregador).isPresent()) {
            throw new RegraDeNegocioException("Não é possível ficar indisponível com uma rota em andamento");
        }

        entregadorDao.atualizarStatus(cdEntregador, request.status());
        entregador.setStatus(request.status());
        return EntregadorMapper.toDisponibilidade(entregador);
    }

    private Entregador buscarOuFalhar(int cdEntregador) throws SQLException {
        Optional<Entregador> entregador = entregadorDao.buscarPorId(cdEntregador);
        if (entregador.isEmpty()) {
            throw new RecursoNaoEncontradoException("Entregador não encontrado");
        }
        return entregador.get();
    }
}
