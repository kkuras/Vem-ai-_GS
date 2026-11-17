package br.com.fiap.vem_ai.controller;

import br.com.fiap.vem_ai.dto.ClienteAtualizacaoRequest;
import br.com.fiap.vem_ai.dto.ClienteRequest;
import br.com.fiap.vem_ai.dto.ClienteResponse;
import br.com.fiap.vem_ai.exception.ApiException;
import br.com.fiap.vem_ai.exception.RespostaErro;
import br.com.fiap.vem_ai.service.ClienteService;
import br.com.fiap.vem_ai.service.EncomendaService;
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
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final EncomendaService encomendaService;

    public ClienteController(ClienteService clienteService, EncomendaService encomendaService) {
        this.clienteService = clienteService;
        this.encomendaService = encomendaService;
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@Valid @RequestBody ClienteRequest request, BindingResult resultado) {
        if (resultado.hasErrors()) {
            return RespostaErro.validacao(resultado);
        }
        try {
            ClienteResponse resposta = clienteService.cadastrar(request);
            return ResponseEntity.created(URI.create("/clientes/" + resposta.cdCliente())).body(resposta);
        } catch (ApiException | SQLException e) {
            return RespostaErro.de(e);
        }
    }

    @GetMapping
    public ResponseEntity<?> listar() {
        try {
            return ResponseEntity.ok(clienteService.listar());
        } catch (ApiException | SQLException e) {
            return RespostaErro.de(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable("id") int id) {
        try {
            return ResponseEntity.ok(clienteService.buscarPorId(id));
        } catch (ApiException | SQLException e) {
            return RespostaErro.de(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable("id") int id,
                                       @Valid @RequestBody ClienteAtualizacaoRequest request,
                                       BindingResult resultado) {
        if (resultado.hasErrors()) {
            return RespostaErro.validacao(resultado);
        }
        try {
            return ResponseEntity.ok(clienteService.atualizar(id, request));
        } catch (ApiException | SQLException e) {
            return RespostaErro.de(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remover(@PathVariable("id") int id) {
        try {
            clienteService.remover(id);
            return ResponseEntity.noContent().build();
        } catch (ApiException | SQLException e) {
            return RespostaErro.de(e);
        }
    }

    @GetMapping("/{id}/encomendas")
    public ResponseEntity<?> encomendas(@PathVariable("id") int id) {
        try {
            return ResponseEntity.ok(encomendaService.listarDoCliente(id));
        } catch (ApiException | SQLException e) {
            return RespostaErro.de(e);
        }
    }
}
