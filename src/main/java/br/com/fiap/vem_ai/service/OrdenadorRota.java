package br.com.fiap.vem_ai.service;

import br.com.fiap.vem_ai.models.Cliente;
import br.com.fiap.vem_ai.models.Encomenda;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrdenadorRota {

    private static final double RAIO_DA_TERRA_KM = 6371.0;

    public static List<Encomenda> ordenar(double latOrigem, double lonOrigem,
                                          List<Encomenda> encomendas,
                                          Map<Integer, Cliente> clientesPorCodigo) {
        List<Encomenda> restantes = new ArrayList<>(encomendas);
        List<Encomenda> ordenadas = new ArrayList<>();
        double latAtual = latOrigem;
        double lonAtual = lonOrigem;

        while (!restantes.isEmpty()) {
            Encomenda maisProxima = restantes.get(0);
            double menorDistancia = Double.MAX_VALUE;

            for (Encomenda encomenda : restantes) {
                Cliente cliente = clientesPorCodigo.get(encomenda.getCdCliente());
                double distancia = distanciaKm(latAtual, lonAtual,
                        cliente.getVlLatitude(), cliente.getVlLongitude());
                if (distancia < menorDistancia) {
                    menorDistancia = distancia;
                    maisProxima = encomenda;
                }
            }

            Cliente destino = clientesPorCodigo.get(maisProxima.getCdCliente());
            latAtual = destino.getVlLatitude();
            lonAtual = destino.getVlLongitude();
            ordenadas.add(maisProxima);
            restantes.remove(maisProxima);
        }
        return ordenadas;
    }

    static double distanciaKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return RAIO_DA_TERRA_KM * c;
    }
}
