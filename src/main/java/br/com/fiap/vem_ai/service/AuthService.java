package br.com.fiap.vem_ai.service;

import br.com.fiap.vem_ai.dao.ClienteDao;
import br.com.fiap.vem_ai.dao.EntregadorDao;
import br.com.fiap.vem_ai.dto.LoginRequest;
import br.com.fiap.vem_ai.dto.LoginResponse;
import br.com.fiap.vem_ai.exception.CredenciaisInvalidasException;
import br.com.fiap.vem_ai.mapper.LoginMapper;
import br.com.fiap.vem_ai.models.Usuario;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class AuthService {

    private final EntregadorDao entregadorDao;
    private final ClienteDao clienteDao;
    private final SenhaService senhaService;

    public AuthService(EntregadorDao entregadorDao, ClienteDao clienteDao, SenhaService senhaService) {
        this.entregadorDao = entregadorDao;
        this.clienteDao = clienteDao;
        this.senhaService = senhaService;
    }

    public LoginResponse login(LoginRequest request) throws SQLException {
        Usuario usuario = switch (request.tipo()) {
            case ENTREGADOR -> entregadorDao.buscarPorEmail(request.dsEmail()).orElse(null);
            case CLIENTE -> clienteDao.buscarPorEmail(request.dsEmail()).orElse(null);
        };

        if (usuario == null || !senhaService.confere(request.senha(), usuario.getDsSenhaHash())) {
            throw new CredenciaisInvalidasException();
        }
        return LoginMapper.toResponse(usuario);
    }
}
