package com.estoqueti.core.repository.jdbc;

import com.estoqueti.core.config.DatabaseConfig;
import com.estoqueti.core.exception.DAOException;
import com.estoqueti.core.model.NotaFiscal;
import com.estoqueti.core.repository.NotaFiscalRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NotaFiscalRepositoryJDBC implements NotaFiscalRepository {

    @Override
    public List<NotaFiscal> listarTodas() {
        String sql = "SELECT * FROM notas_fiscais";
        List<NotaFiscal> lista = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement st = conn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
            return lista;
        } catch (SQLException e) {
            throw new DAOException("Erro ao listar notas fiscais.", e);
        }
    }

    @Override
    public NotaFiscal salvar(NotaFiscal nf) {
        String sql = "INSERT INTO notas_fiscais (numero_nf, data_emissao, valor_total, id_fornecedor) " +
                "VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencher(st, nf);
            st.executeUpdate();
            try (ResultSet keys = st.getGeneratedKeys()) {
                if (keys.next()) nf.setIdNf(keys.getInt(1));
            }
            return nf;
        } catch (SQLException e) {
            throw new DAOException("Erro ao salvar nota fiscal.", e);
        }
    }

    @Override
    public void atualizar(NotaFiscal nf) {
        String sql = "UPDATE notas_fiscais SET numero_nf=?, data_emissao=?, valor_total=?, id_fornecedor=? " +
                "WHERE id_nf=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            preencher(st, nf);
            st.setInt(5, nf.getIdNf());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erro ao atualizar nota fiscal id=" + nf.getIdNf(), e);
        }
    }

    @Override
    public Optional<NotaFiscal> buscarPorId(int idNf) {
        String sql = "SELECT * FROM notas_fiscais WHERE id_nf = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, idNf);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao buscar nota fiscal id=" + idNf, e);
        }
    }

    @Override
    public void remover(int idNf) {
        String sql = "DELETE FROM notas_fiscais WHERE id_nf=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, idNf);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erro ao remover nota fiscal id=" + idNf, e);
        }
    }

    private void preencher(PreparedStatement st, NotaFiscal nf) throws SQLException {
        st.setString(1, nf.getNumeroNf());
        st.setDate(2, Date.valueOf(nf.getDataEmissao()));
        st.setBigDecimal(3, nf.getValorTotal());
        st.setInt(4, nf.getIdFornecedor());
    }

    private NotaFiscal mapear(ResultSet rs) throws SQLException {
        NotaFiscal nf = new NotaFiscal();
        nf.setIdNf(rs.getInt("id_nf"));
        nf.setNumeroNf(rs.getString("numero_nf"));
        nf.setDataEmissao(rs.getDate("data_emissao").toLocalDate());
        nf.setValorTotal(rs.getBigDecimal("valor_total"));
        nf.setIdFornecedor(rs.getInt("id_fornecedor"));
        return nf;
    }
}
