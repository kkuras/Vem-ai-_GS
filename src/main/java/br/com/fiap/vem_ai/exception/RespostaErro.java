package br.com.fiap.vem_ai.exception;

import br.com.fiap.vem_ai.dto.ErroResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class RespostaErro {

    private static final Logger LOG = LoggerFactory.getLogger(RespostaErro.class);

    public static ResponseEntity<ErroResponse> de(Exception e) {
        if (e instanceof ApiException) {
            ApiException api = (ApiException) e;
            return montar(api.getStatus(), api.getErro(), api.getMessage(), null);
        }
        if (e instanceof SQLException) {
            return deBanco((SQLException) e);
        }
        LOG.error("Erro inesperado", e);
        return montar(500, "Erro interno", "Erro inesperado. Tente novamente mais tarde.", null);
    }

    public static ResponseEntity<ErroResponse> validacao(BindingResult resultado) {
        Map<String, String> campos = new LinkedHashMap<>();
        for (FieldError erro : resultado.getFieldErrors()) {
            campos.put(erro.getField(), erro.getDefaultMessage());
        }
        return montar(400, "Validação", "Campos inválidos", campos);
    }

    private static ResponseEntity<ErroResponse> deBanco(SQLException e) {
        switch (e.getErrorCode()) {
            case 1:
                return montar(409, "Conflito", "Já existe um registro com este valor", null);
            case 2291:
                return montar(400, "Requisição inválida", "Um dos registros informados não existe", null);
            case 2292:
                return montar(409, "Conflito", "Não é possível remover: existem registros vinculados", null);
            default:
                LOG.error("Erro de banco de dados", e);
                return montar(500, "Erro interno", "Erro ao acessar o banco de dados", null);
        }
    }

    private static ResponseEntity<ErroResponse> montar(int status, String erro, String mensagem,
                                                       Map<String, String> campos) {
        return ResponseEntity.status(status).body(new ErroResponse(status, erro, mensagem, campos));
    }
}
