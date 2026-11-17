package br.com.fiap.vem_ai.service;

import br.com.fiap.vem_ai.dao.ConnectionFactory;
import br.com.fiap.vem_ai.dao.CentroDistribuicaoDao;
import br.com.fiap.vem_ai.dao.ClienteDao;
import br.com.fiap.vem_ai.dao.EncomendaDao;
import br.com.fiap.vem_ai.dao.EntregadorDao;
import br.com.fiap.vem_ai.dao.EventoRastreioDao;
import br.com.fiap.vem_ai.dao.LoteDao;
import br.com.fiap.vem_ai.dto.IniciarRotaRequest;
import br.com.fiap.vem_ai.dto.IniciarRotaResponse;
import br.com.fiap.vem_ai.dto.LocalizacaoRequest;
import br.com.fiap.vem_ai.dto.LoteRequest;
import br.com.fiap.vem_ai.dto.LoteResponse;
import br.com.fiap.vem_ai.dto.LoteResumoResponse;
import br.com.fiap.vem_ai.enums.StatusLote;
import br.com.fiap.vem_ai.enums.TipoEvento;
import br.com.fiap.vem_ai.exception.AcessoNegadoException;
import br.com.fiap.vem_ai.exception.RecursoNaoEncontradoException;
import br.com.fiap.vem_ai.exception.RegraDeNegocioException;
import br.com.fiap.vem_ai.mapper.LoteMapper;
import br.com.fiap.vem_ai.mapper.RotaMapper;
import br.com.fiap.vem_ai.models.CentroDistribuicao;
import br.com.fiap.vem_ai.models.Cliente;
import br.com.fiap.vem_ai.models.Encomenda;
import br.com.fiap.vem_ai.models.Entregador;
import br.com.fiap.vem_ai.models.EventoRastreio;
import br.com.fiap.vem_ai.models.Lote;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class LoteService {

    private final LoteDao loteDao;
    private final EncomendaDao encomendaDao;
    private final EntregadorDao entregadorDao;
    private final ClienteDao clienteDao;
    private final CentroDistribuicaoDao centroDao;
    private final EventoRastreioDao eventoDao;

    public LoteService(LoteDao loteDao, EncomendaDao encomendaDao, EntregadorDao entregadorDao,
                       ClienteDao clienteDao, CentroDistribuicaoDao centroDao, EventoRastreioDao eventoDao) {
        this.loteDao = loteDao;
        this.encomendaDao = encomendaDao;
        this.entregadorDao = entregadorDao;
        this.clienteDao = clienteDao;
        this.centroDao = centroDao;
        this.eventoDao = eventoDao;
    }

    public LoteResponse cadastrar(LoteRequest request) throws SQLException {
        exigirCentro(request.cdCentro());
        exigirEntregador(request.cdEntregador());

        Lote lote = LoteMapper.toModel(request);
        if (lote.getStatus() == null) {
            lote.setStatus(StatusLote.MONTAGEM);
        }
        lote.setCodigoQr(GeradorCodigo.gerarCodigoQr());
        return LoteMapper.toResponse(loteDao.inserir(lote));
    }

    public List<LoteResponse> listar() throws SQLException {
        List<LoteResponse> respostas = new ArrayList<>();
        for (Lote lote : loteDao.listar()) {
            respostas.add(LoteMapper.toResponse(lote));
        }
        return respostas;
    }

    public LoteResponse buscarPorId(int cdLote) throws SQLException {
        return LoteMapper.toResponse(buscarOuFalhar(cdLote));
    }

    public LoteResponse atualizar(int cdLote, LoteRequest request) throws SQLException {
        Lote existente = buscarOuFalhar(cdLote);
        exigirCentro(request.cdCentro());
        exigirEntregador(request.cdEntregador());

        Lote lote = LoteMapper.toModel(request);
        lote.setCdLote(cdLote);
        if (lote.getStatus() == null) {
            lote.setStatus(existente.getStatus());
        }
        loteDao.atualizar(lote);
        return buscarPorId(cdLote);
    }

    public void remover(int cdLote) throws SQLException {
        if (!loteDao.remover(cdLote)) {
            throw new RecursoNaoEncontradoException("Lote não encontrado");
        }
    }

    public List<LoteResumoResponse> listarResumo(StatusLote status, int cdCentro) throws SQLException {
        exigirCentro(cdCentro);

        List<LoteResumoResponse> resumos = new ArrayList<>();
        for (Lote lote : loteDao.listarPorStatusECentro(status, cdCentro)) {
            Entregador entregador = entregadorDao.buscarPorId(lote.getCdEntregador()).orElse(null);
            String nome = entregador == null ? "" : entregador.getNmEntregador();
            int quantidade = encomendaDao.contarPorLote(lote.getCdLote());
            resumos.add(LoteMapper.toResumo(lote, nome, quantidade));
        }
        return resumos;
    }

    public IniciarRotaResponse iniciarRota(IniciarRotaRequest request) throws SQLException {
        Optional<Lote> encontrado = loteDao.buscarPorCodigoQr(request.codigoQr());
        if (encontrado.isEmpty()) {
            throw new RecursoNaoEncontradoException("QR code inválido");
        }
        Lote lote = encontrado.get();

        if (lote.getCdEntregador() != request.cdEntregador()) {
            throw new AcessoNegadoException("Este lote pertence a outro entregador");
        }
        if (!lote.podeIniciarRota()) {
            throw new RegraDeNegocioException("O lote já foi iniciado ou concluído");
        }

        List<Encomenda> encomendas = encomendaDao.listarPorLote(lote.getCdLote());
        if (encomendas.isEmpty()) {
            throw new RegraDeNegocioException("O lote não tem encomendas");
        }

        Map<Integer, Cliente> clientes = carregarClientes(encomendas);
        CentroDistribuicao centro = exigirCentro(lote.getCdCentro());
        List<Encomenda> ordenadas = OrdenadorRota.ordenar(
                centro.getVlLatitude(), centro.getVlLongitude(), encomendas, clientes);

        try (Connection con = ConnectionFactory.obterconexao()) {
            try {
                con.setAutoCommit(false);

                int posicao = 1;
                for (Encomenda encomenda : ordenadas) {
                    encomendaDao.colocarEmRota(con, encomenda.getCdEncomenda(), posicao);
                    registrarEvento(con, encomenda.getCdEncomenda(), TipoEvento.EM_ROTA);
                    posicao++;
                }
                loteDao.iniciarRota(con, lote.getCdLote(), LocalDateTime.now());

                con.commit();
            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            }
        }
        return montarRota(lote.getCdLote());
    }

    public IniciarRotaResponse loteAtual(int cdEntregador) throws SQLException {
        exigirEntregador(cdEntregador);

        Optional<Lote> lote = loteDao.buscarEmRotaDoEntregador(cdEntregador);
        if (lote.isEmpty()) {
            throw new RecursoNaoEncontradoException("O entregador não tem rota em andamento");
        }
        return montarRota(lote.get().getCdLote());
    }

    public LoteResponse atualizarLocalizacao(int cdLote, LocalizacaoRequest request) throws SQLException {
        Lote lote = buscarOuFalhar(cdLote);
        if (!lote.estaEmRota()) {
            throw new RegraDeNegocioException("O lote não está em rota");
        }
        loteDao.atualizarPosicao(cdLote, request.latitude(), request.longitude());
        return buscarPorId(cdLote);
    }

    private IniciarRotaResponse montarRota(int cdLote) throws SQLException {
        Lote lote = buscarOuFalhar(cdLote);
        List<Encomenda> encomendas = encomendaDao.listarPorLote(cdLote);
        return RotaMapper.toResponse(lote, encomendas, carregarClientes(encomendas));
    }

    private Map<Integer, Cliente> carregarClientes(List<Encomenda> encomendas) throws SQLException {
        Map<Integer, Cliente> clientes = new HashMap<>();
        for (Encomenda encomenda : encomendas) {
            int cdCliente = encomenda.getCdCliente();
            if (!clientes.containsKey(cdCliente)) {
                Optional<Cliente> cliente = clienteDao.buscarPorId(cdCliente);
                if (cliente.isEmpty()) {
                    throw new RecursoNaoEncontradoException("Cliente " + cdCliente + " não encontrado");
                }
                clientes.put(cdCliente, cliente.get());
            }
        }
        return clientes;
    }

    private void registrarEvento(Connection con, int cdEncomenda, TipoEvento tipo) throws SQLException {
        EventoRastreio evento = new EventoRastreio();
        evento.setCdEncomenda(cdEncomenda);
        evento.setTipo(tipo);
        eventoDao.inserir(con, evento);
    }

    private Lote buscarOuFalhar(int cdLote) throws SQLException {
        Optional<Lote> lote = loteDao.buscarPorId(cdLote);
        if (lote.isEmpty()) {
            throw new RecursoNaoEncontradoException("Lote não encontrado");
        }
        return lote.get();
    }

    private CentroDistribuicao exigirCentro(int cdCentro) throws SQLException {
        Optional<CentroDistribuicao> centro = centroDao.buscarPorId(cdCentro);
        if (centro.isEmpty()) {
            throw new RecursoNaoEncontradoException("Centro de distribuição não encontrado");
        }
        return centro.get();
    }

    private Entregador exigirEntregador(int cdEntregador) throws SQLException {
        Optional<Entregador> entregador = entregadorDao.buscarPorId(cdEntregador);
        if (entregador.isEmpty()) {
            throw new RecursoNaoEncontradoException("Entregador não encontrado");
        }
        return entregador.get();
    }
}
