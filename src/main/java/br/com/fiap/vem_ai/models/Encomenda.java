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
    private int cdLote;
    private int cdCliente;
    private String codigoRastreio;
    private Integer nrPosicaoRota;
    private StatusEncomenda status;

    public boolean estaAtiva() {
        return status != StatusEncomenda.ENTREGUE && status != StatusEncomenda.FALHA;
    }

    public boolean foiEntregue() {
        return status == StatusEncomenda.ENTREGUE;
    }

    public int calcularParadasRestantes(int entregasConcluidas) {
        if (nrPosicaoRota == null) {
            throw new IllegalStateException("A encomenda ainda não tem posição na rota");
        }
        return Math.max(0, nrPosicaoRota - entregasConcluidas);
    }

    public boolean deveAvisarCliente(int paradasRestantes) {
        return paradasRestantes == PARADAS_AVISO;
    }
}
