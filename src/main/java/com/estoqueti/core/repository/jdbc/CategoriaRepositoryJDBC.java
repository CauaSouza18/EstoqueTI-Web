package com.estoqueti.core.repository.jdbc;

import com.estoqueti.core.config.DatabaseConfig;
import com.estoqueti.core.exception.DAOException;
import com.estoqueti.core.model.Categoria;
import com.estoqueti.core.repository.CategoriaRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoriaRepositoryJDBC implements CategoriaRepository {

    @Override
    public List<Categoria> listarTodas() {
        String sql = "SELECT * FROM categorias";
        List<Categoria> lista = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement st = conn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
            return lista;
        } catch (SQLException e) {
            throw new DAOException("Erro ao listar categorias.", e);
        }
    }

    @Override
    public Optional<Categoria> buscarPorId(int idCategoria) {
        String sql = "SELECT * FROM categorias WHERE id_categoria = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, idCategoria);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao buscar categoria id=" + idCategoria, e);
        }
    }

    @Override
    public Categoria salvar(Categoria categoria) {
        String sql = "INSERT INTO categorias (nome_categoria) VALUES (?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, categoria.getNomeCategoria());
            st.executeUpdate();
            try (ResultSet keys = st.getGeneratedKeys()) {
                if (keys.next()) categoria.setIdCategoria(keys.getInt(1));
            }
            return categoria;
        } catch (SQLException e) {
            throw new DAOException("Erro ao salvar categoria.", e);
        }
    }

    @Override
    public void atualizar(Categoria categoria) {
        String sql = "UPDATE categorias SET nome_categoria=? WHERE id_categoria=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, categoria.getNomeCategoria());
            st.setInt(2, categoria.getIdCategoria());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erro ao atualizar categoria id=" + categoria.getIdCategoria(), e);
        }
    }

    @Override
    public void remover(int idCategoria) {
        String sql = "DELETE FROM categorias WHERE id_categoria=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, idCategoria);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erro ao remover categoria id=" + idCategoria, e);
        }
    }

    private Categoria mapear(ResultSet rs) throws SQLException {
        return new Categoria(rs.getInt("id_categoria"), rs.getString("nome_categoria"));
    }
}
