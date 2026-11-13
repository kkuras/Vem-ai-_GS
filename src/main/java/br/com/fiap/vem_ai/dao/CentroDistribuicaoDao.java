package br.com.fiap.vem_ai.dao;

import br.com.fiap.vem_ai.config.ConnectionFactory;
import br.com.fiap.vem_ai.models.CentroDistribuicao;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class CentroDistribuicaoDao {

    private static final String COLUNAS =
            "CD_CENTRO, NM_CENTRO, DS_ENDERECO, VL_LATITUDE, VL_LONGITUDE";

    public CentroDistribuicao inserir(CentroDistribuicao centro) throws SQLException {
        String sql = "INSERT INTO TB_CENTRO_DISTRIBUICAO (" + COLUNAS + ") VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ConnectionFactory.obterconexao()) {
            centro.setCdCentro(proximoId(con));
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, centro.getCdCentro());
                ps.setString(2, centro.getNmCentro());
                ps.setString(3, centro.getDsEndereco());
                ps.setDouble(4, centro.getVlLatitude());
                ps.setDouble(5, centro.getVlLongitude());
                ps.executeUpdate();
            }
        }
        return centro;
    }

    public List<CentroDistribuicao> listar() throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM TB_CENTRO_DISTRIBUICAO ORDER BY CD_CENTRO";
        List<CentroDistribuicao> centros = new ArrayList<>();
        try (Connection con = ConnectionFactory.obterconexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                centros.add(mapear(rs));
            }
        }
        return centros;
    }

    public Optional<CentroDistribuicao> buscarPorId(int cdCentro) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM TB_CENTRO_DISTRIBUICAO WHERE CD_CENTRO = ?";
        try (Connection con = ConnectionFactory.obterconexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cdCentro);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
            }
        }
        return Optional.empty();
    }

    public boolean atualizar(CentroDistribuicao centro) throws SQLException {

        String sql = "UPDATE TB_CENTRO_DISTRIBUICAO "
                + "SET NM_CENTRO = ?, DS_ENDERECO = ?, VL_LATITUDE = ?, VL_LONGITUDE = ? "
                + "WHERE CD_CENTRO = ?";
        try (Connection con = ConnectionFactory.obterconexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, centro.getNmCentro());
            ps.setString(2, centro.getDsEndereco());
            ps.setDouble(3, centro.getVlLatitude());
            ps.setDouble(4, centro.getVlLongitude());
            ps.setInt(5, centro.getCdCentro());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean remover(int cdCentro) throws SQLException {
        String sql = "DELETE FROM TB_CENTRO_DISTRIBUICAO WHERE CD_CENTRO = ?";
        try (Connection con = ConnectionFactory.obterconexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cdCentro);
            return ps.executeUpdate() > 0;
        }
    }

    private int proximoId(Connection con) throws SQLException {
        String sql = "SELECT SQ_TB_CENTRO_DISTRIBUICAO.NEXTVAL FROM DUAL";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private CentroDistribuicao mapear(ResultSet rs) throws SQLException {
        CentroDistribuicao centro = new CentroDistribuicao();
        centro.setCdCentro(rs.getInt("CD_CENTRO"));
        centro.setNmCentro(rs.getString("NM_CENTRO"));
        centro.setDsEndereco(rs.getString("DS_ENDERECO"));
        centro.setVlLatitude(rs.getDouble("VL_LATITUDE"));
        centro.setVlLongitude(rs.getDouble("VL_LONGITUDE"));
        return centro;
    }
}
