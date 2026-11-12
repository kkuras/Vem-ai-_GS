package br.com.fiap.vem_ai.models;

import br.com.fiap.vem_ai.enums.TipoUsuario;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cliente extends Usuario{

    private int cdCliente;
    private int cdRegiao;
    private String nmCliente;
    private String nrWhatsapp;
    private String dsEndereco;
    private double vlLatitude;
    private double vlLongitude;

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public String getNome() {
        return "";
    }

    @Override
    public TipoUsuario getTipo() {
        return null;
    }
}
