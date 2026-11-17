package br.com.fiap.vem_ai.controller;

import br.com.fiap.vem_ai.dto.DisponibilidadeRequest;
import br.com.fiap.vem_ai.dto.EntregadorAtualizacaoRequest;
import br.com.fiap.vem_ai.dto.EntregadorRequest;
import br.com.fiap.vem_ai.dto.EntregadorResponse;
import br.com.fiap.vem_ai.exception.ApiException;
import br.com.fiap.vem_ai.exception.RespostaErro;
import br.com.fiap.vem_ai.service.EntregadorService;
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
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.sql.SQLException;

@RestController
@RequestMapping("/entregadores")
public class EntregadorController {

    private final EntregadorService entregadorService;
    private final LoteService loteService;

    public EntregadorController(EntregadorService entregadorService, LoteService loteService) {
        this.entregadorService = entregadorService;
        this.loteService = loteService;
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@Valid @RequestBody EntregadorRequest request, BindingResult resultado) {
        if (resultado.hasErrors()) {
            return RespostaErro.validacao(resultado);
        }
        try {
            EntregadorResponse resposta = entregadorService.cadastrar(request);
            return ResponseEntity.created(URI.create("/entregadores/" + resposta.cdEntregador())).body(resposta);
        } catch (ApiException | SQLException e) {
            return RespostaErro.de(e);
        }
    }

    @GetMapping
    public ResponseEntity<?> listar() {
        try {
            return ResponseEntity.ok(entregadorService.listar());
        } catch (ApiException | SQLException e) {
            return RespostaErro.de(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable("id") int id) {
        try {
            return ResponseEntity.ok(entregadorService.buscarPorId(id));
        } catch (ApiException | SQLException e) {
            return RespostaErro.de(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable("id") int id,
                                       @Valid @RequestBody EntregadorAtualizacaoRequest request,
                                       BindingResult resultado) {
        if (resultado.hasErrors()) {
            return RespostaErro.validacao(resultado);
        }
        try {
            return ResponseEntity.ok(entregadorService.atualizar(id, request));
        } catch (ApiException | SQLException e) {
            return RespostaErro.de(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remover(@PathVariable("id") int id) {
        try {
            entregadorService.remover(id);
            return ResponseEntity.noContent().build();
        } catch (ApiException | SQLException e) {
            return RespostaErro.de(e);
        }
    }

    @PutMapping("/{id}/disponibilidade")
    public ResponseEntity<?> alterarDisponibilidade(@PathVariable("id") int id,
                                                    @Valid @RequestBody DisponibilidadeRequest request,
                                                    BindingResult resultado) {
        if (resultado.hasErrors()) {
            return RespostaErro.validacao(resultado);
        }
        try {
            return ResponseEntity.ok(entregadorService.alterarDisponibilidade(id, request));
        } catch (ApiException | SQLException e) {
            return RespostaErro.de(e);
        }
    }

    @GetMapping("/{id}/lote-atual")
    public ResponseEntity<?> loteAtual(@PathVariable("id") int id) {
        try {
            return ResponseEntity.ok(loteService.loteAtual(id));
        } catch (ApiException | SQLException e) {
            return RespostaErro.de(e);
        }
    }
}
