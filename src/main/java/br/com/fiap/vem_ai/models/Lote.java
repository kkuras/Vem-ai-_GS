package br.com.fiap.vem_ai.models;

import br.com.fiap.vem_ai.enums.StatusLote;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Lote {

    private int cdLote;
    private int cdCentro;
    private int cdEntregador;
    private String codigoQr;
    private StatusLote status;
    private LocalDateTime dtCriacao;
    private LocalDateTime dtInicioRota;
    private Double vlLatitudeAtual;
    private Double vlLongitudeAtual;

    public boolean podeIniciarRota() {
        return status == StatusLote.MONTAGEM;
    }

    public boolean estaEmRota() {
        return status == StatusLote.EM_ROTA;
    }

    public boolean temPosicaoAtual() {
        return vlLatitudeAtual != null && vlLongitudeAtual != null;
    }

}
