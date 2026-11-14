package br.com.fiap.vem_ai.dao;

import br.com.fiap.vem_ai.config.ConnectionFactory;
import br.com.fiap.vem_ai.models.Cliente;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ClienteDao {

    private static final String COLUNAS =
            "CD_CLIENTE, CD_REGIAO, NM_CLIENTE, DS_EMAIL, DS_SENHA_HASH, "
                    + "NR_WHATSAPP, DS_ENDERECO, VL_LATITUDE, VL_LONGITUDE";

    public Cliente inserir(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO TB_CLIENTE (" + COLUNAS + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConnectionFactory.obterconexao()) {
            cliente.setCdCliente(proximoId(con));
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, cliente.getCdCliente());
                ps.setInt(2, cliente.getCdRegiao());
                ps.setString(3, cliente.getNmCliente());
                ps.setString(4, cliente.getDsEmail());
                ps.setString(5, cliente.getDsSenhaHash());
                ps.setString(6, cliente.getNrWhatsapp());
                ps.setString(7, cliente.getDsEndereco());
                ps.setDouble(8, cliente.getVlLatitude());
                ps.setDouble(9, cliente.getVlLongitude());
                ps.executeUpdate();
            }
        }
        return cliente;
    }

    public List<Cliente> listar() throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM TB_CLIENTE ORDER BY CD_CLIENTE";
        List<Cliente> clientes = new ArrayList<>();
        try (Connection con = ConnectionFactory.obterconexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                clientes.add(mapear(rs));
            }
        }
        return clientes;
    }

    public Optional<Cliente> buscarPorId(int cdCliente) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM TB_CLIENTE WHERE CD_CLIENTE = ?";
        try (Connection con = ConnectionFactory.obterconexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cdCliente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Cliente> buscarPorEmail(String email) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM TB_CLIENTE WHERE DS_EMAIL = ?";
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

    public boolean atualizar(Cliente cliente) throws SQLException {
        String sql = "UPDATE TB_CLIENTE "
                + "SET CD_REGIAO = ?, NM_CLIENTE = ?, DS_EMAIL = ?, NR_WHATSAPP = ?, "
                + "DS_ENDERECO = ?, VL_LATITUDE = ?, VL_LONGITUDE = ? "
                + "WHERE CD_CLIENTE = ?";
        try (Connection con = ConnectionFactory.obterconexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cliente.getCdRegiao());
            ps.setString(2, cliente.getNmCliente());
            ps.setString(3, cliente.getDsEmail());
            ps.setString(4, cliente.getNrWhatsapp());
            ps.setString(5, cliente.getDsEndereco());
            ps.setDouble(6, cliente.getVlLatitude());
            ps.setDouble(7, cliente.getVlLongitude());
            ps.setInt(8, cliente.getCdCliente());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean remover(int cdCliente) throws SQLException {
        String sql = "DELETE FROM TB_CLIENTE WHERE CD_CLIENTE = ?";
        try (Connection con = ConnectionFactory.obterconexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cdCliente);
            return ps.executeUpdate() > 0;
        }
    }

    private int proximoId(Connection con) throws SQLException {
        String sql = "SELECT SQ_TB_CLIENTE.NEXTVAL FROM DUAL";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private Cliente mapear(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setCdCliente(rs.getInt("CD_CLIENTE"));
        cliente.setCdRegiao(rs.getInt("CD_REGIAO"));
        cliente.setNmCliente(rs.getString("NM_CLIENTE"));
        cliente.setDsEmail(rs.getString("DS_EMAIL"));
        cliente.setDsSenhaHash(rs.getString("DS_SENHA_HASH"));
        cliente.setNrWhatsapp(rs.getString("NR_WHATSAPP"));
        cliente.setDsEndereco(rs.getString("DS_ENDERECO"));
        cliente.setVlLatitude(rs.getDouble("VL_LATITUDE"));
        cliente.setVlLongitude(rs.getDouble("VL_LONGITUDE"));
        return cliente;
    }
}
