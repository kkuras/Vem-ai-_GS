package br.com.fiap.vem_ai.service;

import br.com.fiap.vem_ai.dao.EncomendaDao;
import br.com.fiap.vem_ai.dao.EventoRastreioDao;
import br.com.fiap.vem_ai.dto.EventoRequest;
import br.com.fiap.vem_ai.dto.EventoResponse;
import br.com.fiap.vem_ai.exception.RecursoNaoEncontradoException;
import br.com.fiap.vem_ai.mapper.EventoMapper;
import br.com.fiap.vem_ai.models.EventoRastreio;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EventoService {

    private final EventoRastreioDao eventoDao;
    private final EncomendaDao encomendaDao;

    public EventoService(EventoRastreioDao eventoDao, EncomendaDao encomendaDao) {
        this.eventoDao = eventoDao;
        this.encomendaDao = encomendaDao;
    }

    public EventoResponse cadastrar(int cdEncomenda, EventoRequest request) throws SQLException {
        exigirEncomenda(cdEncomenda);
        EventoRastreio evento = EventoMapper.toModel(request, cdEncomenda);
        return EventoMapper.toResponse(eventoDao.inserir(evento));
    }

    public List<EventoResponse> listarPorEncomenda(int cdEncomenda) throws SQLException {
        exigirEncomenda(cdEncomenda);
        return converter(eventoDao.listarPorEncomenda(cdEncomenda));
    }

    public List<EventoResponse> listar() throws SQLException {
        return converter(eventoDao.listar());
    }

    public EventoResponse buscarPorId(int cdEvento) throws SQLException {
        return EventoMapper.toResponse(buscarOuFalhar(cdEvento));
    }

    public EventoResponse atualizar(int cdEvento, EventoRequest request) throws SQLException {
        EventoRastreio evento = buscarOuFalhar(cdEvento);
        evento.setTipo(request.tipo());
        eventoDao.atualizar(evento);
        return EventoMapper.toResponse(evento);
    }

    public void remover(int cdEvento) throws SQLException {
        if (!eventoDao.remover(cdEvento)) {
            throw new RecursoNaoEncontradoException("Evento não encontrado");
        }
    }

    private EventoRastreio buscarOuFalhar(int cdEvento) throws SQLException {
        Optional<EventoRastreio> evento = eventoDao.buscarPorId(cdEvento);
        if (evento.isEmpty()) {
            throw new RecursoNaoEncontradoException("Evento não encontrado");
        }
        return evento.get();
    }

    private void exigirEncomenda(int cdEncomenda) throws SQLException {
        if (encomendaDao.buscarPorId(cdEncomenda).isEmpty()) {
            throw new RecursoNaoEncontradoException("Encomenda não encontrada");
        }
    }

    private List<EventoResponse> converter(List<EventoRastreio> eventos) {
        List<EventoResponse> respostas = new ArrayList<>();
        for (EventoRastreio evento : eventos) {
            respostas.add(EventoMapper.toResponse(evento));
        }
        return respostas;
    }
}
