package br.com.fiap.vem_ai.dao;

import br.com.fiap.vem_ai.config.ConnectionFactory;
import br.com.fiap.vem_ai.enums.StatusEncomenda;
import br.com.fiap.vem_ai.models.Encomenda;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class EncomendaDao {

    private static final String COLUNAS =
            "CD_ENCOMENDA, CD_CENTRO, CD_LOTE, CD_CLIENTE, "
                    + "DS_CODIGO_RASTREIO, NR_POSICAO_ROTA, DS_STATUS";

    public Encomenda inserir(Encomenda encomenda) throws SQLException {
        try (Connection con = ConnectionFactory.obterconexao()) {
            return inserir(con, encomenda);
        }
    }

    public Encomenda inserir(Connection con, Encomenda encomenda) throws SQLException {
        String sql = "INSERT INTO TB_ENCOMENDA (" + COLUNAS + ") VALUES (?, ?, ?, ?, ?, ?, ?)";
        encomenda.setCdEncomenda(proximoId(con));
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, encomenda.getCdEncomenda());
            ps.setInt(2, encomenda.getCdCentro());
            ps.setInt(3, encomenda.getCdLote());
            ps.setInt(4, encomenda.getCdCliente());
            ps.setString(5, encomenda.getCodigoRastreio());

            if (encomenda.getNrPosicaoRota() == null) {
                ps.setNull(6, Types.NUMERIC);
            } else {
                ps.setInt(6, encomenda.getNrPosicaoRota());
            }
            ps.setString(7, encomenda.getStatus().name());
            ps.executeUpdate();
        }
        return encomenda;
    }

    public List<Encomenda> listar() throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM TB_ENCOMENDA ORDER BY CD_ENCOMENDA";
        List<Encomenda> encomendas = new ArrayList<>();
        try (Connection con = ConnectionFactory.obterconexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                encomendas.add(mapear(rs));
            }
        }
        return encomendas;
    }

    public Optional<Encomenda> buscarPorId(int cdEncomenda) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM TB_ENCOMENDA WHERE CD_ENCOMENDA = ?";
        try (Connection con = ConnectionFactory.obterconexao()) {
            return buscarUma(con, sql, cdEncomenda);
        }
    }

    public Optional<Encomenda> buscarPorCodigoRastreio(String codigoRastreio) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM TB_ENCOMENDA WHERE DS_CODIGO_RASTREIO = ?";
        try (Connection con = ConnectionFactory.obterconexao()) {
            return buscarUma(con, sql, codigoRastreio);
        }
    }

    public List<Encomenda> listarPorLote(int cdLote) throws SQLException {
        try (Connection con = ConnectionFactory.obterconexao()) {
            return listarPorLote(con, cdLote);
        }
    }

    public List<Encomenda> listarPorLote(Connection con, int cdLote) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM TB_ENCOMENDA WHERE CD_LOTE = ? "
                + "ORDER BY NR_POSICAO_ROTA, CD_ENCOMENDA";
        return listarComParametro(con, sql, cdLote);
    }

    public List<Encomenda> listarPorCliente(int cdCliente) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM TB_ENCOMENDA WHERE CD_CLIENTE = ? "
                + "ORDER BY CD_ENCOMENDA DESC";
        try (Connection con = ConnectionFactory.obterconexao()) {
            return listarComParametro(con, sql, cdCliente);
        }
    }

    public int contarPorLote(int cdLote) throws SQLException {
        try (Connection con = ConnectionFactory.obterconexao()) {
            return contarPorLote(con, cdLote);
        }
    }

    public int contarPorLote(Connection con, int cdLote) throws SQLException {
        String sql = "SELECT COUNT(*) FROM TB_ENCOMENDA WHERE CD_LOTE = ?";
        return contar(con, sql, cdLote);
    }

    public int contarParadasConcluidas(Connection con, int cdLote) throws SQLException {
        String sql = "SELECT COUNT(*) FROM TB_ENCOMENDA "
                + "WHERE CD_LOTE = ? AND DS_STATUS IN ('ENTREGUE', 'FALHA')";
        return contar(con, sql, cdLote);
    }

    public boolean atualizar(Encomenda encomenda) throws SQLException {
        String sql = "UPDATE TB_ENCOMENDA "
                + "SET CD_CENTRO = ?, CD_LOTE = ?, CD_CLIENTE = ?, NR_POSICAO_ROTA = ?, DS_STATUS = ? "
                + "WHERE CD_ENCOMENDA = ?";
        try (Connection con = ConnectionFactory.obterconexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, encomenda.getCdCentro());
            ps.setInt(2, encomenda.getCdLote());
            ps.setInt(3, encomenda.getCdCliente());
            if (encomenda.getNrPosicaoRota() == null) {
                ps.setNull(4, Types.NUMERIC);
            } else {
                ps.setInt(4, encomenda.getNrPosicaoRota());
            }
            ps.setString(5, encomenda.getStatus().name());
            ps.setInt(6, encomenda.getCdEncomenda());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean colocarEmRota(Connection con, int cdEncomenda, int posicao) throws SQLException {
        String sql = "UPDATE TB_ENCOMENDA SET NR_POSICAO_ROTA = ?, DS_STATUS = 'EM_ROTA' "
                + "WHERE CD_ENCOMENDA = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, posicao);
            ps.setInt(2, cdEncomenda);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean atualizarStatus(Connection con, int cdEncomenda, StatusEncomenda status)
            throws SQLException {
        String sql = "UPDATE TB_ENCOMENDA SET DS_STATUS = ? WHERE CD_ENCOMENDA = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, cdEncomenda);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean remover(int cdEncomenda) throws SQLException {
        String sql = "DELETE FROM TB_ENCOMENDA WHERE CD_ENCOMENDA = ?";
        try (Connection con = ConnectionFactory.obterconexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cdEncomenda);
            return ps.executeUpdate() > 0;
        }
    }

    private Optional<Encomenda> buscarUma(Connection con, String sql, Object parametro)
            throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setObject(1, parametro);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
            }
        }
        return Optional.empty();
    }

    private List<Encomenda> listarComParametro(Connection con, String sql, int parametro)
            throws SQLException {
        List<Encomenda> encomendas = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, parametro);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    encomendas.add(mapear(rs));
                }
            }
        }
        return encomendas;
    }

    private int contar(Connection con, String sql, int parametro) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, parametro);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private int proximoId(Connection con) throws SQLException {
        String sql = "SELECT SQ_TB_ENCOMENDA.NEXTVAL FROM DUAL";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private Encomenda mapear(ResultSet rs) throws SQLException {
        Encomenda encomenda = new Encomenda();
        encomenda.setCdEncomenda(rs.getInt("CD_ENCOMENDA"));
        encomenda.setCdCentro(rs.getInt("CD_CENTRO"));
        encomenda.setCdLote(rs.getInt("CD_LOTE"));
        encomenda.setCdCliente(rs.getInt("CD_CLIENTE"));
        encomenda.setCodigoRastreio(rs.getString("DS_CODIGO_RASTREIO"));

        int posicao = rs.getInt("NR_POSICAO_ROTA");
        encomenda.setNrPosicaoRota(rs.wasNull() ? null : posicao);

        encomenda.setStatus(StatusEncomenda.valueOf(rs.getString("DS_STATUS")));
        return encomenda;
    }
}
