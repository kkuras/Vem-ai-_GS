package br.com.fiap.vem_ai.service;

import br.com.fiap.vem_ai.dao.CentroDistribuicaoDao;
import br.com.fiap.vem_ai.dto.CentroRequest;
import br.com.fiap.vem_ai.dto.CentroResponse;
import br.com.fiap.vem_ai.exception.RecursoNaoEncontradoException;
import br.com.fiap.vem_ai.mapper.CentroMapper;
import br.com.fiap.vem_ai.models.CentroDistribuicao;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CentroService {

    private final CentroDistribuicaoDao centroDao;

    public CentroService(CentroDistribuicaoDao centroDao) {
        this.centroDao = centroDao;
    }

    public CentroResponse cadastrar(CentroRequest request) throws SQLException {
        CentroDistribuicao centro = CentroMapper.toModel(request);
        return CentroMapper.toResponse(centroDao.inserir(centro));
    }

    public List<CentroResponse> listar() throws SQLException {
        List<CentroResponse> respostas = new ArrayList<>();
        for (CentroDistribuicao centro : centroDao.listar()) {
            respostas.add(CentroMapper.toResponse(centro));
        }
        return respostas;
    }

    public CentroResponse buscarPorId(int cdCentro) throws SQLException {
        Optional<CentroDistribuicao> centro = centroDao.buscarPorId(cdCentro);
        if (centro.isEmpty()) {
            throw new RecursoNaoEncontradoException("Centro de distribuição não encontrado");
        }
        return CentroMapper.toResponse(centro.get());
    }

    public CentroResponse atualizar(int cdCentro, CentroRequest request) throws SQLException {
        CentroDistribuicao centro = CentroMapper.toModel(request);
        centro.setCdCentro(cdCentro);
        if (!centroDao.atualizar(centro)) {
            throw new RecursoNaoEncontradoException("Centro de distribuição não encontrado");
        }
        return CentroMapper.toResponse(centro);
    }

    public void remover(int cdCentro) throws SQLException {
        if (!centroDao.remover(cdCentro)) {
            throw new RecursoNaoEncontradoException("Centro de distribuição não encontrado");
        }
    }
}
