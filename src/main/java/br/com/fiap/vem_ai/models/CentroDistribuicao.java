package br.com.fiap.vem_ai.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CentroDistribuicao {

    private int cdCentro;
    private String nmCentro;
    private String dsEndereco;
    private double vlLatitude;
    private double vlLongitude;
}
