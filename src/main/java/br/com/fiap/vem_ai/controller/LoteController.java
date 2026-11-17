package br.com.fiap.vem_ai.controller;

import br.com.fiap.vem_ai.dto.IniciarRotaRequest;
import br.com.fiap.vem_ai.dto.LocalizacaoRequest;
import br.com.fiap.vem_ai.dto.LoteRequest;
import br.com.fiap.vem_ai.dto.LoteResponse;
import br.com.fiap.vem_ai.enums.StatusLote;
import br.com.fiap.vem_ai.exception.ApiException;
import br.com.fiap.vem_ai.exception.RespostaErro;
import br.com.fiap.vem_ai.service.EncomendaService;
import br.com.fiap.vem_ai.service.LoteService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.sql.SQLException;

@RestController
@RequestMapping("/lotes")
public class LoteController {

    private final LoteService loteService;
    private final EncomendaService encomendaService;

    public LoteController(LoteService loteService, EncomendaService encomendaService) {
        this.loteService = loteService;
        this.encomendaService = encomendaService;
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@Valid @RequestBody LoteRequest request, BindingResult resultado) {
        if (resultado.hasErrors()) {
            return RespostaErro.validacao(resultado);
        }
        try {
            LoteResponse resposta = loteService.cadastrar(request);
            return ResponseEntity.created(URI.create("/lotes/" + resposta.cdLote())).body(resposta);
        } catch (ApiException | SQLException e) {
            return RespostaErro.de(e);
        }
    }

    @GetMapping
    public ResponseEntity<?> listar(@RequestParam(value = "status", required = false) StatusLote status,
                                    @RequestParam(value = "cdCentro", required = false) Integer cdCentro) {
        try {
            if (status != null && cdCentro != null) {
                return ResponseEntity.ok(loteService.listarResumo(status, cdCentro));
            }
            return ResponseEntity.ok(loteService.listar());
        } catch (ApiException | SQLException e) {
            return RespostaErro.de(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable("id") int id) {
        try {
            return ResponseEntity.ok(loteService.buscarPorId(id));
        } catch (ApiException | SQLException e) {
            return RespostaErro.de(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable("id") int id,
                                       @Valid @RequestBody LoteRequest request, BindingResult resultado) {
        if (resultado.hasErrors()) {
            return RespostaErro.validacao(resultado);
        }
        try {
            return ResponseEntity.ok(loteService.atualizar(id, request));
        } catch (ApiException | SQLException e) {
            return RespostaErro.de(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remover(@PathVariable("id") int id) {
        try {
            loteService.remover(id);
            return ResponseEntity.noContent().build();
        } catch (ApiException | SQLException e) {
            return RespostaErro.de(e);
        }
    }

    @GetMapping("/{id}/encomendas")
    public ResponseEntity<?> encomendas(@PathVariable("id") int id) {
        try {
            return ResponseEntity.ok(encomendaService.listarPorLote(id));
        } catch (ApiException | SQLException e) {
            return RespostaErro.de(e);
        }
    }

    @PostMapping("/iniciar-rota")
    public ResponseEntity<?> iniciarRota(@Valid @RequestBody IniciarRotaRequest request, BindingResult resultado) {
        if (resultado.hasErrors()) {
            return RespostaErro.validacao(resultado);
        }
        try {
            return ResponseEntity.ok(loteService.iniciarRota(request));
        } catch (ApiException | SQLException e) {
            return RespostaErro.de(e);
        }
    }

    @PutMapping("/{id}/localizacao")
    public ResponseEntity<?> atualizarLocalizacao(@PathVariable("id") int id, @Valid @RequestBody LocalizacaoRequest request,
                                                  BindingResult resultado) {
        if (resultado.hasErrors()) {
            return RespostaErro.validacao(resultado);
        }
        try {
            return ResponseEntity.ok(loteService.atualizarLocalizacao(id, request));
        } catch (ApiException | SQLException e) {
            return RespostaErro.de(e);
        }
    }
}
