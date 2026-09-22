package com.estoqueti.core.repository.jdbc;

import com.estoqueti.core.config.DatabaseConfig;
import com.estoqueti.core.exception.DAOException;
import com.estoqueti.core.model.Fornecedor;
import com.estoqueti.core.repository.FornecedorRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FornecedorRepositoryJDBC implements FornecedorRepository {

    @Override
    public List<Fornecedor> listarTodos() {
        String sql = "SELECT * FROM fornecedores";
        List<Fornecedor> lista = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement st = conn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
            return lista;
        } catch (SQLException e) {
            throw new DAOException("Erro ao listar fornecedores.", e);
        }
    }

    @Override
    public Optional<Fornecedor> buscarPorId(int idFornecedor) {
        String sql = "SELECT * FROM fornecedores WHERE id_fornecedor = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, idFornecedor);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao buscar fornecedor id=" + idFornecedor, e);
        }
    }

    @Override
    public Fornecedor salvar(Fornecedor f) {
        String sql = "INSERT INTO fornecedores (nome_fornecedor, endereco, telefone, email, cnpj, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencher(st, f);
            st.executeUpdate();
            try (ResultSet keys = st.getGeneratedKeys()) {
                if (keys.next()) f.setIdFornecedor(keys.getInt(1));
            }
            return f;
        } catch (SQLException e) {
            throw new DAOException("Erro ao salvar fornecedor.", e);
        }
    }

    @Override
    public void atualizar(Fornecedor f) {
        String sql = "UPDATE fornecedores SET nome_fornecedor=?, endereco=?, telefone=?, email=?, cnpj=?, status=? " +
                "WHERE id_fornecedor=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            preencher(st, f);
            st.setInt(7, f.getIdFornecedor());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erro ao atualizar fornecedor id=" + f.getIdFornecedor(), e);
        }
    }

    @Override
    public void remover(int idFornecedor) {
        String sql = "DELETE FROM fornecedores WHERE id_fornecedor=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, idFornecedor);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erro ao remover fornecedor id=" + idFornecedor, e);
        }
    }

    private void preencher(PreparedStatement st, Fornecedor f) throws SQLException {
        st.setString(1, f.getNomeFornecedor());
        st.setString(2, f.getEndereco());
        st.setString(3, f.getTelefone());
        st.setString(4, f.getEmail());
        st.setString(5, f.getCnpj());
        st.setString(6, f.getStatus());
    }

    private Fornecedor mapear(ResultSet rs) throws SQLException {
        return new Fornecedor(
                rs.getInt("id_fornecedor"), rs.getString("nome_fornecedor"),
                rs.getString("endereco"), rs.getString("telefone"),
                rs.getString("email"), rs.getString("cnpj"), rs.getString("status"));
    }
}
