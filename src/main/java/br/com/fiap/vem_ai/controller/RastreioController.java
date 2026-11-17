package br.com.fiap.vem_ai.controller;

import br.com.fiap.vem_ai.exception.ApiException;
import br.com.fiap.vem_ai.exception.RespostaErro;
import br.com.fiap.vem_ai.service.EncomendaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
public class RastreioController {

    private final EncomendaService encomendaService;

    public RastreioController(EncomendaService encomendaService) {
        this.encomendaService = encomendaService;
    }

    @GetMapping("/rastreio/{codigo}")
    public ResponseEntity<?> rastrear(@PathVariable("codigo") String codigo) {
        try {
            return ResponseEntity.ok(encomendaService.rastrear(codigo));
        } catch (ApiException | SQLException e) {
            return RespostaErro.de(e);
        }
    }
}
