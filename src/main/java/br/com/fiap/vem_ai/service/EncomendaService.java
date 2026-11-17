package br.com.fiap.vem_ai.service;

import br.com.fiap.vem_ai.config.ConnectionFactory;
import br.com.fiap.vem_ai.dao.CentroDistribuicaoDao;
import br.com.fiap.vem_ai.dao.ClienteDao;
import br.com.fiap.vem_ai.dao.EncomendaDao;
import br.com.fiap.vem_ai.dao.EntregadorDao;
import br.com.fiap.vem_ai.dao.EventoRastreioDao;
import br.com.fiap.vem_ai.dao.LoteDao;
import br.com.fiap.vem_ai.dto.ClienteEncomendaResponse;
import br.com.fiap.vem_ai.dto.EncomendaAtualizacaoRequest;
import br.com.fiap.vem_ai.dto.EncomendaBipadaResponse;
import br.com.fiap.vem_ai.dto.EncomendaRequest;
import br.com.fiap.vem_ai.dto.EncomendaResponse;
import br.com.fiap.vem_ai.dto.EntregaResponse;
import br.com.fiap.vem_ai.dto.RastreioResponse;
import br.com.fiap.vem_ai.enums.StatusEncomenda;
import br.com.fiap.vem_ai.enums.StatusLote;
import br.com.fiap.vem_ai.enums.TipoEvento;
import br.com.fiap.vem_ai.exception.RecursoNaoEncontradoException;
import br.com.fiap.vem_ai.exception.RegraDeNegocioException;
import br.com.fiap.vem_ai.mapper.EncomendaMapper;
import br.com.fiap.vem_ai.mapper.RastreioMapper;
import br.com.fiap.vem_ai.models.Cliente;
import br.com.fiap.vem_ai.models.Encomenda;
import br.com.fiap.vem_ai.models.Entregador;
import br.com.fiap.vem_ai.models.EventoRastreio;
import br.com.fiap.vem_ai.models.Lote;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EncomendaService {

    private static final int LIMITE_POR_LOTE = 20;

    private final EncomendaDao encomendaDao;
    private final LoteDao loteDao;
    private final EntregadorDao entregadorDao;
    private final ClienteDao clienteDao;
    private final CentroDistribuicaoDao centroDao;
    private final EventoRastreioDao eventoDao;

    public EncomendaService(EncomendaDao encomendaDao, LoteDao loteDao, EntregadorDao entregadorDao,
                            ClienteDao clienteDao, CentroDistribuicaoDao centroDao,
                            EventoRastreioDao eventoDao) {
        this.encomendaDao = encomendaDao;
        this.loteDao = loteDao;
        this.entregadorDao = entregadorDao;
        this.clienteDao = clienteDao;
        this.centroDao = centroDao;
        this.eventoDao = eventoDao;
    }

    public EncomendaBipadaResponse bipar(EncomendaRequest request) throws SQLException {
        exigirCentro(request.cdCentro());
        Cliente cliente = exigirCliente(request.cdCliente());

        List<Entregador> candidatos = entregadorDao.listarDisponiveisForaDeRotaPorRegiao(cliente.getCdRegiao());
        if (candidatos.isEmpty()) {
            throw new RegraDeNegocioException("Nenhum entregador disponível na região");
        }

        Encomenda encomenda = EncomendaMapper.toModel(request);
        Entregador escolhido = null;

        try (Connection con = ConnectionFactory.obterconexao()) {
            try {
                con.setAutoCommit(false);

                Lote lote = null;
                for (Entregador candidato : candidatos) {
                    Optional<Lote> aberto = loteDao.buscarAbertoDoEntregadorParaAtualizar(
                            con, candidato.getCdEntregador());
                    if (aberto.isEmpty()) {
                        escolhido = candidato;
                        break;
                    }
                    int quantidade = encomendaDao.contarPorLote(con, aberto.get().getCdLote());
                    if (quantidade < LIMITE_POR_LOTE) {
                        escolhido = candidato;
                        lote = aberto.get();
                        break;
                    }
                }
                if (escolhido == null) {
                    throw new RegraDeNegocioException("Nenhum entregador com vaga na região");
                }

                if (lote == null) {
                    Lote novo = new Lote();
                    novo.setCdCentro(request.cdCentro());
                    novo.setCdEntregador(escolhido.getCdEntregador());
                    novo.setStatus(StatusLote.MONTAGEM);
                    novo.setCodigoQr(GeradorCodigo.gerarCodigoQr());
                    lote = loteDao.inserir(con, novo);
                }

                encomenda.setCdLote(lote.getCdLote());
                encomenda.setCodigoRastreio(GeradorCodigo.gerarCodigoRastreio());
                encomenda.setStatus(StatusEncomenda.NO_CD);
                encomendaDao.inserir(con, encomenda);

                con.commit();
            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            }
        }
        return EncomendaMapper.toBipada(encomenda, escolhido);
    }

    public EntregaResponse entregar(int cdEncomenda) throws SQLException {
        Encomenda encomenda = buscarOuFalhar(cdEncomenda);
        if (encomenda.getStatus() != StatusEncomenda.EM_ROTA) {
            throw new RegraDeNegocioException("A encomenda não está em rota");
        }

        List<Integer> avisados = new ArrayList<>();
        int restantes = 0;

        try (Connection con = ConnectionFactory.obterconexao()) {
            try {
                con.setAutoCommit(false);

                encomendaDao.atualizarStatus(con, cdEncomenda, StatusEncomenda.ENTREGUE);
                registrarEvento(con, cdEncomenda, TipoEvento.ENTREGUE);

                int concluidas = encomendaDao.contarParadasConcluidas(con, encomenda.getCdLote());
                List<Encomenda> doLote = encomendaDao.listarPorLote(con, encomenda.getCdLote());
                restantes = doLote.size() - concluidas;

                for (Encomenda outra : doLote) {
                    if (outra.getStatus() == StatusEncomenda.EM_ROTA && outra.getNrPosicaoRota() != null) {
                        int paradas = outra.calcularParadasRestantes(concluidas);
                        if (outra.deveAvisarCliente(paradas)) {
                            registrarEvento(con, outra.getCdEncomenda(), TipoEvento.AVISO_ENVIADO);
                            avisados.add(outra.getCdEncomenda());
                        }
                    }
                }

                if (restantes == 0) {
                    loteDao.atualizarStatus(con, encomenda.getCdLote(), StatusLote.CONCLUIDO);
                }

                con.commit();
            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            }
        }

        encomenda.setStatus(StatusEncomenda.ENTREGUE);
        return EncomendaMapper.toEntrega(encomenda, restantes, avisados);
    }

    public RastreioResponse rastrear(String codigoRastreio) throws SQLException {
        Optional<Encomenda> encontrada = encomendaDao.buscarPorCodigoRastreio(codigoRastreio);
        if (encontrada.isEmpty()) {
            throw new RecursoNaoEncontradoException("Encomenda não encontrada");
        }
        Encomenda encomenda = encontrada.get();
        Cliente cliente = exigirCliente(encomenda.getCdCliente());
        Lote lote = exigirLote(encomenda.getCdLote());

        Integer paradas;
        try (Connection con = ConnectionFactory.obterconexao()) {
            paradas = calcularParadas(con, encomenda);
        }

        boolean exibirMapa = paradas != null && paradas <= Encomenda.PARADAS_AVISO;
        return RastreioMapper.toResponse(encomenda, paradas, exibirMapa, lote, cliente);
    }

    public List<ClienteEncomendaResponse> listarDoCliente(int cdCliente) throws SQLException {
        exigirCliente(cdCliente);

        List<ClienteEncomendaResponse> respostas = new ArrayList<>();
        try (Connection con = ConnectionFactory.obterconexao()) {
            for (Encomenda encomenda : encomendaDao.listarPorCliente(cdCliente)) {
                Integer paradas = calcularParadas(con, encomenda);
                respostas.add(EncomendaMapper.toClienteEncomenda(encomenda, paradas));
            }
        }
        return respostas;
    }

    public List<EncomendaResponse> listar() throws SQLException {
        return converter(encomendaDao.listar());
    }

    public EncomendaResponse buscarPorId(int cdEncomenda) throws SQLException {
        return EncomendaMapper.toResponse(buscarOuFalhar(cdEncomenda));
    }

    public List<EncomendaResponse> listarPorLote(int cdLote) throws SQLException {
        exigirLote(cdLote);
        return converter(encomendaDao.listarPorLote(cdLote));
    }

    public EncomendaResponse atualizar(int cdEncomenda, EncomendaAtualizacaoRequest request) throws SQLException {
        buscarOuFalhar(cdEncomenda);
        exigirCentro(request.cdCentro());
        exigirLote(request.cdLote());
        exigirCliente(request.cdCliente());

        Encomenda encomenda = EncomendaMapper.toModel(request);
        encomenda.setCdEncomenda(cdEncomenda);
        encomendaDao.atualizar(encomenda);
        return buscarPorId(cdEncomenda);
    }

    public void remover(int cdEncomenda) throws SQLException {
        if (!encomendaDao.remover(cdEncomenda)) {
            throw new RecursoNaoEncontradoException("Encomenda não encontrada");
        }
    }

    private Integer calcularParadas(Connection con, Encomenda encomenda) throws SQLException {
        if (encomenda.getStatus() != StatusEncomenda.EM_ROTA || encomenda.getNrPosicaoRota() == null) {
            return null;
        }
        int concluidas = encomendaDao.contarParadasConcluidas(con, encomenda.getCdLote());
        return encomenda.calcularParadasRestantes(concluidas);
    }

    private void registrarEvento(Connection con, int cdEncomenda, TipoEvento tipo) throws SQLException {
        EventoRastreio evento = new EventoRastreio();
        evento.setCdEncomenda(cdEncomenda);
        evento.setTipo(tipo);
        eventoDao.inserir(con, evento);
    }

    private List<EncomendaResponse> converter(List<Encomenda> encomendas) {
        List<EncomendaResponse> respostas = new ArrayList<>();
        for (Encomenda encomenda : encomendas) {
            respostas.add(EncomendaMapper.toResponse(encomenda));
        }
        return respostas;
    }

    private Encomenda buscarOuFalhar(int cdEncomenda) throws SQLException {
        Optional<Encomenda> encomenda = encomendaDao.buscarPorId(cdEncomenda);
        if (encomenda.isEmpty()) {
            throw new RecursoNaoEncontradoException("Encomenda não encontrada");
        }
        return encomenda.get();
    }

    private void exigirCentro(int cdCentro) throws SQLException {
        if (centroDao.buscarPorId(cdCentro).isEmpty()) {
            throw new RecursoNaoEncontradoException("Centro de distribuição não encontrado");
        }
    }

    private Cliente exigirCliente(int cdCliente) throws SQLException {
        Optional<Cliente> cliente = clienteDao.buscarPorId(cdCliente);
        if (cliente.isEmpty()) {
            throw new RecursoNaoEncontradoException("Cliente não encontrado");
        }
        return cliente.get();
    }

    private Lote exigirLote(int cdLote) throws SQLException {
        Optional<Lote> lote = loteDao.buscarPorId(cdLote);
        if (lote.isEmpty()) {
            throw new RecursoNaoEncontradoException("Lote não encontrado");
        }
        return lote.get();
    }
}
