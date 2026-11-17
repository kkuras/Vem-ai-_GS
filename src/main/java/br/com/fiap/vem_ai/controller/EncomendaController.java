package br.com.fiap.vem_ai.controller;

import br.com.fiap.vem_ai.dto.EncomendaAtualizacaoRequest;
import br.com.fiap.vem_ai.dto.EncomendaBipadaResponse;
import br.com.fiap.vem_ai.dto.EncomendaRequest;
import br.com.fiap.vem_ai.dto.EventoRequest;
import br.com.fiap.vem_ai.dto.EventoResponse;
import br.com.fiap.vem_ai.exception.ApiException;
import br.com.fiap.vem_ai.exception.RespostaErro;
import br.com.fiap.vem_ai.service.EncomendaService;
import br.com.fiap.vem_ai.service.EventoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.sql.SQLException;

@RestController
@RequestMapping("/encomendas")
public class EncomendaController {

    private final EncomendaService encomendaService;
    private final EventoService eventoService;

    public EncomendaController(EncomendaService encomendaService, EventoService eventoService) {
        this.encomendaService = encomendaService;
        this.eventoService = eventoService;
    }

    @PostMapping
    public ResponseEntity<?> bipar(@Valid @RequestBody EncomendaRequest request, BindingResult resultado) {
        if (resultado.hasErrors()) {
            return RespostaErro.validacao(resultado);
        }
        try {
            EncomendaBipadaResponse resposta = encomendaService.bipar(request);
            return ResponseEntity.created(URI.create("/encomendas/" + resposta.cdEncomenda())).body(resposta);
        } catch (ApiException | SQLException e) {
            return RespostaErro.de(e);
        }
    }

    @GetMapping
    public ResponseEntity<?> listar() {
        try {
            return ResponseEntity.ok(encomendaService.listar());
        } catch (ApiException | SQLException e) {
            return RespostaErro.de(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable("id") int id) {
        try {
            return ResponseEntity.ok(encomendaService.buscarPorId(id));
        } catch (ApiException | SQLException e) {
            return RespostaErro.de(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable("id") int id,
                                       @Valid @RequestBody EncomendaAtualizacaoRequest request,
                                       BindingResult resultado) {
        if (resultado.hasErrors()) {
            return RespostaErro.validacao(resultado);
        }
        try {
            return ResponseEntity.ok(encomendaService.atualizar(id, request));
        } catch (ApiException | SQLException e) {
            return RespostaErro.de(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remover(@PathVariable("id") int id) {
        try {
            encomendaService.remover(id);
            return ResponseEntity.noContent().build();
        } catch (ApiException | SQLException e) {
            return RespostaErro.de(e);
        }
    }

    @PostMapping("/{id}/entrega")
    public ResponseEntity<?> entregar(@PathVariable("id") int id) {
        try {
            return ResponseEntity.ok(encomendaService.entregar(id));
        } catch (ApiException | SQLException e) {
            return RespostaErro.de(e);
        }
    }

    @GetMapping("/{id}/eventos")
    public ResponseEntity<?> eventos(@PathVariable("id") int id) {
        try {
            return ResponseEntity.ok(eventoService.listarPorEncomenda(id));
        } catch (ApiException | SQLException e) {
            return RespostaErro.de(e);
        }
    }

    @PostMapping("/{id}/eventos")
    public ResponseEntity<?> cadastrarEvento(@PathVariable("id") int id,
                                             @Valid @RequestBody EventoRequest request, BindingResult resultado) {
        if (resultado.hasErrors()) {
            return RespostaErro.validacao(resultado);
        }
        try {
            EventoResponse resposta = eventoService.cadastrar(id, request);
            return ResponseEntity.created(URI.create("/eventos/" + resposta.cdEvento())).body(resposta);
        } catch (ApiException | SQLException e) {
            return RespostaErro.de(e);
        }
    }
}
