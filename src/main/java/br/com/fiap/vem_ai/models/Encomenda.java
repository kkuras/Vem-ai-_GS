package br.com.fiap.vem_ai.models;

import br.com.fiap.vem_ai.enums.StatusEncomenda;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Encomenda {

    private static int PARADAS_AVISO = 3;
    private int cdEncomenda;
    private int cdCentro;
    private int cdCliente;
    private String codigoRastreio;
    private Integer nrPosicaoRota;
    private StatusEncomenda status;

    public boolean estaAtiva() {
        return true;
    }

    public boolean foiEntregue() {
        return true;
    }

    public int calcularParadasRestantes(int entregasConcluidas) {
        return entregasConcluidas;
    }

    public boolean deveAvisarCliente(int paradasRestantes) {
        return true;
    }
}
