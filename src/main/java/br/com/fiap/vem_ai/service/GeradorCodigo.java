package br.com.fiap.vem_ai.service;

import java.security.SecureRandom;


public class GeradorCodigo {

    private static final String CARACTERES = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom ALEATORIO = new SecureRandom();

    public static String gerarCodigoQr() {
        return "LOTE-" + sufixo(6);
    }

    public static String gerarCodigoRastreio() {
        return "PC-" + sufixo(6);
    }

    private static String sufixo(int tamanho) {
        StringBuilder texto = new StringBuilder();
        for (int i = 0; i < tamanho; i++) {
            texto.append(CARACTERES.charAt(ALEATORIO.nextInt(CARACTERES.length())));
        }
        return texto.toString();
    }
}
