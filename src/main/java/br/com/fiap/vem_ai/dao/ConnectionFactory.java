package br.com.fiap.vem_ai.dao;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ConnectionFactory {

    private static final Map<String, String> ARQUIVO_ENV = lerArquivoEnv();

    public static Connection obterconexao() {
        try {
            return DriverManager.getConnection(
                    lerVariavel("SPRING_DATASOURCE_URL"),
                    lerVariavel("SPRING_DATASOURCE_USERNAME"),
                    lerVariavel("SPRING_DATASOURCE_PASSWORD"));
        } catch (SQLException e) {
            throw new RuntimeException("Não foi possível conectar ao banco: " + e.getMessage(), e);
        }
    }

    private static String lerVariavel(String nome) {
        String valor = System.getenv(nome);
        if (valor == null || valor.isBlank()) {
            valor = ARQUIVO_ENV.get(nome);
        }
        if (valor == null || valor.isBlank()) {
            throw new IllegalStateException("Variável de ambiente não definida: " + nome);
        }
        return valor;
    }

    private static Map<String, String> lerArquivoEnv() {
        Map<String, String> valores = new HashMap<>();
        Path caminho = Path.of(".env");
        if (!Files.exists(caminho)) {
            return valores;
        }
        try {
            for (String linha : Files.readAllLines(caminho)) {
                linha = linha.trim();
                if (linha.isEmpty() || linha.startsWith("#") || !linha.contains("=")) {
                    continue;
                }
                int posicao = linha.indexOf('=');
                String chave = linha.substring(0, posicao).trim();
                String valor = linha.substring(posicao + 1).trim();

                boolean entreAspas = valor.length() >= 2
                        && ((valor.startsWith("\"") && valor.endsWith("\""))
                        || (valor.startsWith("'") && valor.endsWith("'")));
                if (entreAspas) {
                    valor = valor.substring(1, valor.length() - 1);
                }
                valores.put(chave, valor);
            }
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível ler o arquivo .env", e);
        }
        return valores;
    }
}