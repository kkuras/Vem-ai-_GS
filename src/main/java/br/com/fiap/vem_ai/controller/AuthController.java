package br.com.fiap.vem_ai.controller;

import br.com.fiap.vem_ai.dto.LoginRequest;
import br.com.fiap.vem_ai.exception.ApiException;
import br.com.fiap.vem_ai.exception.RespostaErro;
import br.com.fiap.vem_ai.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, BindingResult resultado) {
        if (resultado.hasErrors()) {
            return RespostaErro.validacao(resultado);
        }
        try {
            return ResponseEntity.ok(authService.login(request));
        } catch (ApiException | SQLException e) {
            return RespostaErro.de(e);
        }
    }
}
