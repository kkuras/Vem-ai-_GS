package br.com.fiap.vem_ai.dao;

import br.com.fiap.vem_ai.config.ConnectionFactory;
import br.com.fiap.vem_ai.models.Regiao;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class RegiaoDao {

    public Regiao inserir(Regiao regiao) throws SQLException {
        String sql = "INSERT INTO TB_REGIAO (CD_REGIAO, NM_REGIAO) VALUES (?, ?)";
        try (Connection con = ConnectionFactory.obterconexao()) {
            regiao.setCdRegiao(proximoId(con));
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, regiao.getCdRegiao());
                ps.setString(2, regiao.getNmRegiao());
                ps.executeUpdate();
            }
        }
        return regiao;
    }

    public List<Regiao> listar() throws SQLException {
        String sql = "SELECT CD_REGIAO, NM_REGIAO FROM TB_REGIAO ORDER BY CD_REGIAO";
        List<Regiao> regioes = new ArrayList<>();
        try (Connection con = ConnectionFactory.obterconexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                regioes.add(mapear(rs));
            }
        }
        return regioes;
    }

    public Optional<Regiao> buscarPorId(int cdRegiao) throws SQLException {
        String sql = "SELECT CD_REGIAO, NM_REGIAO FROM TB_REGIAO WHERE CD_REGIAO = ?";
        try (Connection con = ConnectionFactory.obterconexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cdRegiao);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
            }
        }
        return Optional.empty();
    }

    public boolean atualizar(Regiao regiao) throws SQLException {
        String sql = "UPDATE TB_REGIAO SET NM_REGIAO = ? WHERE CD_REGIAO = ?";
        try (Connection con = ConnectionFactory.obterconexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, regiao.getNmRegiao());
            ps.setInt(2, regiao.getCdRegiao());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean remover(int cdRegiao) throws SQLException {
        String sql = "DELETE FROM TB_REGIAO WHERE CD_REGIAO = ?";
        try (Connection con = ConnectionFactory.obterconexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cdRegiao);
            return ps.executeUpdate() > 0;
        }
    }

    private int proximoId(Connection con) throws SQLException {
        String sql = "SELECT SQ_TB_REGIAO.NEXTVAL FROM DUAL";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private Regiao mapear(ResultSet rs) throws SQLException {
        Regiao regiao = new Regiao();
        regiao.setCdRegiao(rs.getInt("CD_REGIAO"));
        regiao.setNmRegiao(rs.getString("NM_REGIAO"));
        return regiao;
    }
}