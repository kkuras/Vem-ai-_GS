package br.com.fiap.vem_ai.dao;

import br.com.fiap.vem_ai.config.ConnectionFactory;
import br.com.fiap.vem_ai.enums.StatusLote;
import br.com.fiap.vem_ai.models.Lote;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class LoteDao {

    private static final String COLUNAS =
            "CD_LOTE, CD_CENTRO, CD_ENTREGADOR, DS_CODIGO_QR, DS_STATUS, "
                    + "DT_CRIACAO, DT_INICIO_ROTA, VL_LATITUDE_ATUAL, VL_LONGITUDE_ATUAL";

    public Lote inserir(Lote lote) throws SQLException {
        try (Connection con = ConnectionFactory.obterconexao()) {
            return inserir(con, lote);
        }
    }

    public Lote inserir(Connection con, Lote lote) throws SQLException {
        String sql = "INSERT INTO TB_LOTE "
                + "(CD_LOTE, CD_CENTRO, CD_ENTREGADOR, DS_CODIGO_QR, DS_STATUS, DT_CRIACAO) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        lote.setCdLote(proximoId(con));
        if (lote.getDtCriacao() == null) {
            lote.setDtCriacao(LocalDateTime.now());
        }
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, lote.getCdLote());
            ps.setInt(2, lote.getCdCentro());
            ps.setInt(3, lote.getCdEntregador());
            ps.setString(4, lote.getCodigoQr());
            ps.setString(5, lote.getStatus().name());
            ps.setTimestamp(6, Timestamp.valueOf(lote.getDtCriacao()));
            ps.executeUpdate();
        }
        return lote;
    }

    public List<Lote> listar() throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM TB_LOTE ORDER BY CD_LOTE";
        List<Lote> lotes = new ArrayList<>();
        try (Connection con = ConnectionFactory.obterconexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lotes.add(mapear(rs));
            }
        }
        return lotes;
    }

    public Optional<Lote> buscarPorId(int cdLote) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM TB_LOTE WHERE CD_LOTE = ?";
        try (Connection con = ConnectionFactory.obterconexao()) {
            return buscarUm(con, sql, cdLote);
        }
    }

    public Optional<Lote> buscarPorCodigoQr(String codigoQr) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM TB_LOTE WHERE DS_CODIGO_QR = ?";
        try (Connection con = ConnectionFactory.obterconexao()) {
            return buscarUm(con, sql, codigoQr);
        }
    }

    public List<Lote> listarPorStatusECentro(StatusLote status, int cdCentro) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM TB_LOTE "
                + "WHERE DS_STATUS = ? AND CD_CENTRO = ? ORDER BY CD_LOTE";
        List<Lote> lotes = new ArrayList<>();
        try (Connection con = ConnectionFactory.obterconexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, cdCentro);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lotes.add(mapear(rs));
                }
            }
        }
        return lotes;
    }

    public Optional<Lote> buscarAbertoDoEntregador(int cdEntregador) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM TB_LOTE "
                + "WHERE CD_ENTREGADOR = ? AND DS_STATUS = 'MONTAGEM'";
        try (Connection con = ConnectionFactory.obterconexao()) {
            return buscarUm(con, sql, cdEntregador);
        }
    }

    public Optional<Lote> buscarAbertoDoEntregadorParaAtualizar(Connection con, int cdEntregador)
            throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM TB_LOTE "
                + "WHERE CD_ENTREGADOR = ? AND DS_STATUS = 'MONTAGEM' FOR UPDATE";
        return buscarUm(con, sql, cdEntregador);
    }

    public Optional<Lote> buscarEmRotaDoEntregador(int cdEntregador) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM TB_LOTE "
                + "WHERE CD_ENTREGADOR = ? AND DS_STATUS = 'EM_ROTA'";
        try (Connection con = ConnectionFactory.obterconexao()) {
            return buscarUm(con, sql, cdEntregador);
        }
    }

    public boolean atualizar(Lote lote) throws SQLException {
        String sql = "UPDATE TB_LOTE SET CD_CENTRO = ?, CD_ENTREGADOR = ?, DS_STATUS = ? "
                + "WHERE CD_LOTE = ?";
        try (Connection con = ConnectionFactory.obterconexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, lote.getCdCentro());
            ps.setInt(2, lote.getCdEntregador());
            ps.setString(3, lote.getStatus().name());
            ps.setInt(4, lote.getCdLote());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean iniciarRota(Connection con, int cdLote, LocalDateTime inicio) throws SQLException {
        String sql = "UPDATE TB_LOTE SET DS_STATUS = 'EM_ROTA', DT_INICIO_ROTA = ? "
                + "WHERE CD_LOTE = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(inicio));
            ps.setInt(2, cdLote);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean atualizarStatus(Connection con, int cdLote, StatusLote status) throws SQLException {
        String sql = "UPDATE TB_LOTE SET DS_STATUS = ? WHERE CD_LOTE = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, cdLote);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean atualizarPosicao(int cdLote, double latitude, double longitude) throws SQLException {
        String sql = "UPDATE TB_LOTE SET VL_LATITUDE_ATUAL = ?, VL_LONGITUDE_ATUAL = ? "
                + "WHERE CD_LOTE = ?";
        try (Connection con = ConnectionFactory.obterconexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, latitude);
            ps.setDouble(2, longitude);
            ps.setInt(3, cdLote);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean remover(int cdLote) throws SQLException {
        String sql = "DELETE FROM TB_LOTE WHERE CD_LOTE = ?";
        try (Connection con = ConnectionFactory.obterconexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cdLote);
            return ps.executeUpdate() > 0;
        }
    }

    private Optional<Lote> buscarUm(Connection con, String sql, Object parametro) throws SQLException {
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

    private int proximoId(Connection con) throws SQLException {
        String sql = "SELECT SQ_TB_LOTE.NEXTVAL FROM DUAL";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private Lote mapear(ResultSet rs) throws SQLException {
        Lote lote = new Lote();
        lote.setCdLote(rs.getInt("CD_LOTE"));
        lote.setCdCentro(rs.getInt("CD_CENTRO"));
        lote.setCdEntregador(rs.getInt("CD_ENTREGADOR"));
        lote.setCodigoQr(rs.getString("DS_CODIGO_QR"));
        lote.setStatus(StatusLote.valueOf(rs.getString("DS_STATUS")));
        lote.setDtCriacao(rs.getTimestamp("DT_CRIACAO").toLocalDateTime());

        Timestamp inicio = rs.getTimestamp("DT_INICIO_ROTA");
        lote.setDtInicioRota(inicio == null ? null : inicio.toLocalDateTime());

        lote.setVlLatitudeAtual(lerDouble(rs, "VL_LATITUDE_ATUAL"));
        lote.setVlLongitudeAtual(lerDouble(rs, "VL_LONGITUDE_ATUAL"));
        return lote;
    }

    private Double lerDouble(ResultSet rs, String coluna) throws SQLException {
        double valor = rs.getDouble(coluna);
        return rs.wasNull() ? null : valor;
    }
}
