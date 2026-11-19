package br.com.fiap.vem_ai.handler;

import br.com.fiap.vem_ai.dto.ErroResponse;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Substitui a página de erro padrão do Spring (/error).
 *
 * Os erros de negócio já são tratados nos controllers. Este controller cobre os que
 * escapam antes de chegar neles: JSON mal formatado, id inválido na URL, método HTTP
 * não suportado, rota inexistente e falhas inesperadas. Assim todo erro sai no mesmo
 * JSON padronizado (ErroResponse), nunca como página HTML, stack trace ou SQL.
 */
@RestController
public class ErroController implements ErrorController {

    private static final Logger LOG = LoggerFactory.getLogger(ErroController.class);

    @RequestMapping("/error")
    public ResponseEntity<ErroResponse> erro(HttpServletRequest request) {
        Object codigo = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int status = codigo instanceof Integer valor ? valor : 500;

        Object excecao = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        if (status >= 500 && excecao instanceof Throwable causa) {
            // o detalhe fica só no log do servidor, nunca na resposta
            LOG.error("Erro inesperado em {}", request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI), causa);
        }

        ErroResponse corpo = switch (status) {
            case 400 -> new ErroResponse(400, "Requisição inválida",
                    "Verifique o JSON enviado e os parâmetros da URL", null);
            case 404 -> new ErroResponse(404, "Não encontrado", "Rota não encontrada", null);
            case 405 -> new ErroResponse(405, "Método não permitido",
                    "Este método HTTP não é suportado nesta rota", null);
            case 415 -> new ErroResponse(415, "Tipo de conteúdo não suportado",
                    "Envie o corpo em JSON (Content-Type: application/json)", null);
            default -> status >= 500
                    ? new ErroResponse(500, "Erro interno", "Erro inesperado. Tente novamente mais tarde.", null)
                    : new ErroResponse(status, "Erro", "Não foi possível processar a requisição", null);
        };

        // Content-Type fixo: mesmo que o navegador peça HTML, a resposta é sempre JSON
        return ResponseEntity.status(corpo.status())
                .contentType(MediaType.APPLICATION_JSON)
                .body(corpo);
    }
}
