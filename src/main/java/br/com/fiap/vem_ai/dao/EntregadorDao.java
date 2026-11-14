package br.com.fiap.vem_ai.dao;

import br.com.fiap.vem_ai.config.ConnectionFactory;
import br.com.fiap.vem_ai.enums.StatusEntregador;
import br.com.fiap.vem_ai.models.Entregador;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class EntregadorDao {

    private static final String COLUNAS =
            "CD_ENTREGADOR, NM_ENTREGADOR, DS_EMAIL, DS_SENHA_HASH, NR_TELEFONE, DS_STATUS";

    private static final String COLUNAS_COM_APELIDO =
            "e.CD_ENTREGADOR, e.NM_ENTREGADOR, e.DS_EMAIL, e.DS_SENHA_HASH, e.NR_TELEFONE, e.DS_STATUS";

    // ------------------------------------------------------------------
    // INSERIR (duas versões: uma abre a própria conexão, a outra recebe
    // a conexão pronta para ser usada dentro de uma transação)
    // ------------------------------------------------------------------

    public Entregador inserir(Entregador entregador) throws SQLException {
        try (Connection con = ConnectionFactory.obterconexao()) {
            return inserir(con, entregador);
        }
    }

    public Entregador inserir(Connection con, Entregador entregador) throws SQLException {
        String sql = "INSERT INTO TB_ENTREGADOR (" + COLUNAS + ") VALUES (?, ?, ?, ?, ?, ?)";
        entregador.setCdEntregador(proximoId(con));
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, entregador.getCdEntregador());
            ps.setString(2, entregador.getNmEntregador());
            ps.setString(3, entregador.getDsEmail());
            ps.setString(4, entregador.getDsSenhaHash());
            ps.setString(5, entregador.getNrTelefone());
            ps.setString(6, entregador.getStatus().name());
            ps.executeUpdate();
        }
        return entregador;
    }

    public void vincularRegiao(Connection con, int cdEntregador, int cdRegiao) throws SQLException {
        String sql = "INSERT INTO TB_ENTREGADOR_REGIAO (CD_ENTREGADOR, CD_REGIAO) VALUES (?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cdEntregador);
            ps.setInt(2, cdRegiao);
            ps.executeUpdate();
        }
    }

    public List<Entregador> listar() throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM TB_ENTREGADOR ORDER BY CD_ENTREGADOR";
        List<Entregador> entregadores = new ArrayList<>();
        try (Connection con = ConnectionFactory.obterconexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                entregadores.add(mapear(rs));
            }
        }
        return entregadores;
    }

    public Optional<Entregador> buscarPorId(int cdEntregador) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM TB_ENTREGADOR WHERE CD_ENTREGADOR = ?";
        try (Connection con = ConnectionFactory.obterconexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cdEntregador);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Entregador> buscarPorEmail(String email) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM TB_ENTREGADOR WHERE DS_EMAIL = ?";
        try (Connection con = ConnectionFactory.obterconexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Integer> listarCodigosRegioes(int cdEntregador) throws SQLException {
        String sql = "SELECT CD_REGIAO FROM TB_ENTREGADOR_REGIAO "
                + "WHERE CD_ENTREGADOR = ? ORDER BY CD_REGIAO";
        List<Integer> codigos = new ArrayList<>();
        try (Connection con = ConnectionFactory.obterconexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cdEntregador);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    codigos.add(rs.getInt("CD_REGIAO"));
                }
            }
        }
        return codigos;
    }

    public List<Entregador> listarDisponiveisForaDeRotaPorRegiao(int cdRegiao) throws SQLException {
        String sql = "SELECT " + COLUNAS_COM_APELIDO + " FROM TB_ENTREGADOR e "
                + "JOIN TB_ENTREGADOR_REGIAO er ON er.CD_ENTREGADOR = e.CD_ENTREGADOR "
                + "WHERE er.CD_REGIAO = ? "
                + "AND e.DS_STATUS = 'DISPONIVEL' "
                + "AND NOT EXISTS (SELECT 1 FROM TB_LOTE l "
                + "                WHERE l.CD_ENTREGADOR = e.CD_ENTREGADOR "
                + "                AND l.DS_STATUS = 'EM_ROTA') "
                + "ORDER BY e.CD_ENTREGADOR";
        List<Entregador> entregadores = new ArrayList<>();
        try (Connection con = ConnectionFactory.obterconexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cdRegiao);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    entregadores.add(mapear(rs));
                }
            }
        }
        return entregadores;
    }

    public boolean atualizar(Entregador entregador) throws SQLException {
        String sql = "UPDATE TB_ENTREGADOR "
                + "SET NM_ENTREGADOR = ?, DS_EMAIL = ?, NR_TELEFONE = ? "
                + "WHERE CD_ENTREGADOR = ?";
        try (Connection con = ConnectionFactory.obterconexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, entregador.getNmEntregador());
            ps.setString(2, entregador.getDsEmail());
            ps.setString(3, entregador.getNrTelefone());
            ps.setInt(4, entregador.getCdEntregador());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean atualizarStatus(int cdEntregador, StatusEntregador status) throws SQLException {
        String sql = "UPDATE TB_ENTREGADOR SET DS_STATUS = ? WHERE CD_ENTREGADOR = ?";
        try (Connection con = ConnectionFactory.obterconexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, cdEntregador);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean remover(int cdEntregador) throws SQLException {
        try (Connection con = ConnectionFactory.obterconexao()) {
            try {
                con.setAutoCommit(false);
                removerVinculos(con, cdEntregador);
                boolean removido = removerEntregador(con, cdEntregador);
                con.commit();
                return removido;
            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            }
        }
    }

    private void removerVinculos(Connection con, int cdEntregador) throws SQLException {
        String sql = "DELETE FROM TB_ENTREGADOR_REGIAO WHERE CD_ENTREGADOR = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cdEntregador);
            ps.executeUpdate();
        }
    }

    private boolean removerEntregador(Connection con, int cdEntregador) throws SQLException {
        String sql = "DELETE FROM TB_ENTREGADOR WHERE CD_ENTREGADOR = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cdEntregador);
            return ps.executeUpdate() > 0;
        }
    }

    // ------------------------------------------------------------------
    // AUXILIARES
    // ------------------------------------------------------------------

    private int proximoId(Connection con) throws SQLException {
        String sql = "SELECT SQ_TB_ENTREGADOR.NEXTVAL FROM DUAL";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private Entregador mapear(ResultSet rs) throws SQLException {
        Entregador entregador = new Entregador();
        entregador.setCdEntregador(rs.getInt("CD_ENTREGADOR"));
        entregador.setNmEntregador(rs.getString("NM_ENTREGADOR"));
        entregador.setDsEmail(rs.getString("DS_EMAIL"));
        entregador.setDsSenhaHash(rs.getString("DS_SENHA_HASH"));
        entregador.setNrTelefone(rs.getString("NR_TELEFONE"));
        entregador.setStatus(StatusEntregador.valueOf(rs.getString("DS_STATUS")));
        return entregador;
    }
}
