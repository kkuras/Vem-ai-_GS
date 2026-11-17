package br.com.fiap.vem_ai.controller;

import br.com.fiap.vem_ai.dto.CentroRequest;
import br.com.fiap.vem_ai.dto.CentroResponse;
import br.com.fiap.vem_ai.exception.ApiException;
import br.com.fiap.vem_ai.exception.RespostaErro;
import br.com.fiap.vem_ai.service.CentroService;
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
@RequestMapping("/centros")
public class CentroController {

    private final CentroService centroService;

    public CentroController(CentroService centroService) {
        this.centroService = centroService;
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@Valid @RequestBody CentroRequest request, BindingResult resultado) {

        if (resultado.hasErrors()) {
            return RespostaErro.validacao(resultado);
        }
        try {
            CentroResponse resposta = centroService.cadastrar(request);
            return ResponseEntity.created(URI.create("/centros/" + resposta.cdCentro())).body(resposta);
        } catch (ApiException | SQLException e) {
            return RespostaErro.de(e);
        }
    }

    @GetMapping
    public ResponseEntity<?> listar() {
        try {
            return ResponseEntity.ok(centroService.listar());
        } catch (ApiException | SQLException e) {
            return RespostaErro.de(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable("id") int id) {
        try {
            return ResponseEntity.ok(centroService.buscarPorId(id));
        } catch (ApiException | SQLException e) {
            return RespostaErro.de(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable("id") int id,
                                       @Valid @RequestBody CentroRequest request, BindingResult resultado) {
        if (resultado.hasErrors()) {
            return RespostaErro.validacao(resultado);
        }
        try {
            return ResponseEntity.ok(centroService.atualizar(id, request));
        } catch (ApiException | SQLException e) {
            return RespostaErro.de(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remover(@PathVariable("id") int id) {
        try {
            centroService.remover(id);
            return ResponseEntity.noContent().build();
        } catch (ApiException | SQLException e) {
            return RespostaErro.de(e);
        }
    }
}
