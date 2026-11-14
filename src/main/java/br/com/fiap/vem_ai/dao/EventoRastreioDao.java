package br.com.fiap.vem_ai.dao;

import br.com.fiap.vem_ai.config.ConnectionFactory;
import br.com.fiap.vem_ai.enums.TipoEvento;
import br.com.fiap.vem_ai.models.EventoRastreio;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class EventoRastreioDao {

    private static final String COLUNAS = "CD_EVENTO, CD_ENCOMENDA, DS_TIPO, DT_EVENTO";

    public EventoRastreio inserir(EventoRastreio evento) throws SQLException {
        try (Connection con = ConnectionFactory.obterconexao()) {
            return inserir(con, evento);
        }
    }

    public EventoRastreio inserir(Connection con, EventoRastreio evento) throws SQLException {
        String sql = "INSERT INTO TB_EVENTO_RASTREIO (" + COLUNAS + ") VALUES (?, ?, ?, ?)";
        evento.setCdEvento(proximoId(con));

        if (evento.getDtEvento() == null) {
            evento.setDtEvento(LocalDateTime.now());
        }
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, evento.getCdEvento());
            ps.setInt(2, evento.getCdEncomenda());
            ps.setString(3, evento.getTipo().name());
            ps.setTimestamp(4, Timestamp.valueOf(evento.getDtEvento()));
            ps.executeUpdate();
        }
        return evento;
    }

    public List<EventoRastreio> listar() throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM TB_EVENTO_RASTREIO ORDER BY CD_EVENTO";
        List<EventoRastreio> eventos = new ArrayList<>();
        try (Connection con = ConnectionFactory.obterconexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                eventos.add(mapear(rs));
            }
        }
        return eventos;
    }

    public Optional<EventoRastreio> buscarPorId(int cdEvento) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM TB_EVENTO_RASTREIO WHERE CD_EVENTO = ?";
        try (Connection con = ConnectionFactory.obterconexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cdEvento);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<EventoRastreio> listarPorEncomenda(int cdEncomenda) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM TB_EVENTO_RASTREIO "
                + "WHERE CD_ENCOMENDA = ? ORDER BY DT_EVENTO, CD_EVENTO";
        List<EventoRastreio> eventos = new ArrayList<>();
        try (Connection con = ConnectionFactory.obterconexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cdEncomenda);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    eventos.add(mapear(rs));
                }
            }
        }
        return eventos;
    }

    public boolean atualizar(EventoRastreio evento) throws SQLException {
        String sql = "UPDATE TB_EVENTO_RASTREIO SET DS_TIPO = ? WHERE CD_EVENTO = ?";
        try (Connection con = ConnectionFactory.obterconexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, evento.getTipo().name());
            ps.setInt(2, evento.getCdEvento());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean remover(int cdEvento) throws SQLException {
        String sql = "DELETE FROM TB_EVENTO_RASTREIO WHERE CD_EVENTO = ?";
        try (Connection con = ConnectionFactory.obterconexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cdEvento);
            return ps.executeUpdate() > 0;
        }
    }

    private int proximoId(Connection con) throws SQLException {
        String sql = "SELECT SQ_TB_EVENTO_RASTREIO.NEXTVAL FROM DUAL";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private EventoRastreio mapear(ResultSet rs) throws SQLException {
        EventoRastreio evento = new EventoRastreio();
        evento.setCdEvento(rs.getInt("CD_EVENTO"));
        evento.setCdEncomenda(rs.getInt("CD_ENCOMENDA"));
        evento.setTipo(TipoEvento.valueOf(rs.getString("DS_TIPO")));
        evento.setDtEvento(rs.getTimestamp("DT_EVENTO").toLocalDateTime());
        return evento;
    }
}
