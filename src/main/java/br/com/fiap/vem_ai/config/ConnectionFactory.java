package br.com.fiap.vem_ai.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

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

    // lê a variável de ambiente e avisa com clareza se ela não existir
    private static String lerVariavel(String nome) {
        String valor = System.getenv(nome);
        if (valor == null || valor.isBlank()) {
            throw new IllegalStateException("Variável de ambiente não definida: " + nome);
        }
        return valor;
    }
}