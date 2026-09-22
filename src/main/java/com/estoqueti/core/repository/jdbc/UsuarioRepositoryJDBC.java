package com.estoqueti.core.repository.jdbc;

import com.estoqueti.core.config.DatabaseConfig;
import com.estoqueti.core.exception.DAOException;
import com.estoqueti.core.model.Usuario;
import com.estoqueti.core.repository.UsuarioRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioRepositoryJDBC implements UsuarioRepository {

    @Override
    public List<Usuario> listarTodos() {
        String sql = "SELECT * FROM usuarios";
        List<Usuario> lista = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement st = conn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
            return lista;
        } catch (SQLException e) {
            throw new DAOException("Erro ao listar usuarios.", e);
        }
    }

    @Override
    public Optional<Usuario> buscarPorLogin(String login) {
        String sql = "SELECT * FROM usuarios WHERE login = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, login);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao buscar usuario login=" + login, e);
        }
    }

    @Override
    public Usuario salvar(Usuario u) {
        String sql = "INSERT INTO usuarios (nome_usuario, login, senha, nivel_acesso) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, u.getNomeUsuario());
            st.setString(2, u.getLogin());
            st.setString(3, u.getSenha());
            st.setString(4, u.getNivelAcesso());
            st.executeUpdate();
            try (ResultSet keys = st.getGeneratedKeys()) {
                if (keys.next()) u.setIdUsuario(keys.getInt(1));
            }
            return u;
        } catch (SQLException e) {
            throw new DAOException("Erro ao salvar usuario.", e);
        }
    }

    @Override
    public void atualizar(Usuario u) {
        String sql = "UPDATE usuarios SET nome_usuario=?, login=?, senha=?, nivel_acesso=? WHERE id_usuario=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, u.getNomeUsuario());
            st.setString(2, u.getLogin());
            st.setString(3, u.getSenha());
            st.setString(4, u.getNivelAcesso());
            st.setInt(5, u.getIdUsuario());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erro ao atualizar usuario id=" + u.getIdUsuario(), e);
        }
    }

    @Override
    public Optional<Usuario> buscarPorId(int idUsuario) {
        String sql = "SELECT * FROM usuarios WHERE id_usuario = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, idUsuario);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao buscar usuario id=" + idUsuario, e);
        }
    }

    @Override
    public void remover(int idUsuario) {
        String sql = "DELETE FROM usuarios WHERE id_usuario=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, idUsuario);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erro ao remover usuario id=" + idUsuario, e);
        }
    }

    @Override
    public boolean existeOutroComMesmoLogin(String login, int idUsuarioAtual) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE LOWER(login) = LOWER(?) AND id_usuario <> ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, login);
            st.setInt(2, idUsuarioAtual);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao verificar login duplicado.", e);
        }
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getInt("id_usuario"), rs.getString("nome_usuario"),
                rs.getString("login"), rs.getString("senha"), rs.getString("nivel_acesso"));
    }
}
