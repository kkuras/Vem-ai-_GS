package br.com.fiap.vem_ai.service;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;


@Service
public class SenhaService {

    public String gerarHash(String senha) {
        return BCrypt.hashpw(senha, BCrypt.gensalt());
    }

    public boolean confere(String senhaDigitada, String hash) {
        try {
            return BCrypt.checkpw(senhaDigitada, hash);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
