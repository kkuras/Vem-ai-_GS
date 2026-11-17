package br.com.fiap.vem_ai.service;

import br.com.fiap.vem_ai.dao.RegiaoDao;
import br.com.fiap.vem_ai.dto.RegiaoRequest;
import br.com.fiap.vem_ai.dto.RegiaoResponse;
import br.com.fiap.vem_ai.exception.RecursoNaoEncontradoException;
import br.com.fiap.vem_ai.mapper.RegiaoMapper;
import br.com.fiap.vem_ai.models.Regiao;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RegiaoService {

    private final RegiaoDao regiaoDao;

    public RegiaoService(RegiaoDao regiaoDao) {
        this.regiaoDao = regiaoDao;
    }

    public RegiaoResponse cadastrar(RegiaoRequest request) throws SQLException {
        Regiao regiao = RegiaoMapper.toModel(request);
        return RegiaoMapper.toResponse(regiaoDao.inserir(regiao));
    }

    public List<RegiaoResponse> listar() throws SQLException {
        List<RegiaoResponse> respostas = new ArrayList<>();
        for (Regiao regiao : regiaoDao.listar()) {
            respostas.add(RegiaoMapper.toResponse(regiao));
        }
        return respostas;
    }

    public RegiaoResponse buscarPorId(int cdRegiao) throws SQLException {
        return RegiaoMapper.toResponse(buscarOuFalhar(cdRegiao));
    }

    public RegiaoResponse atualizar(int cdRegiao, RegiaoRequest request) throws SQLException {
        Regiao regiao = RegiaoMapper.toModel(request);
        regiao.setCdRegiao(cdRegiao);
        if (!regiaoDao.atualizar(regiao)) {
            throw new RecursoNaoEncontradoException("Região não encontrada");
        }
        return RegiaoMapper.toResponse(regiao);
    }

    public void remover(int cdRegiao) throws SQLException {
        if (!regiaoDao.remover(cdRegiao)) {
            throw new RecursoNaoEncontradoException("Região não encontrada");
        }
    }

    private Regiao buscarOuFalhar(int cdRegiao) throws SQLException {
        Optional<Regiao> regiao = regiaoDao.buscarPorId(cdRegiao);
        if (regiao.isEmpty()) {
            throw new RecursoNaoEncontradoException("Região não encontrada");
        }
        return regiao.get();
    }
}
