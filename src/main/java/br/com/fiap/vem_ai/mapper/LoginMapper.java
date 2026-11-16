package br.com.fiap.vem_ai.mapper;

import br.com.fiap.vem_ai.dto.LoginResponse;
import br.com.fiap.vem_ai.models.Usuario;

public class LoginMapper {

    public static LoginResponse toResponse(Usuario usuario) {
        return new LoginResponse(usuario.getId(), usuario.getTipo(), usuario.getNome());
    }
}
