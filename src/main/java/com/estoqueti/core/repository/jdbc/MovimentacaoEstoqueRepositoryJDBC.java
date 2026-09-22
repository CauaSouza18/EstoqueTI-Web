package com.estoqueti.core.repository.jdbc;

import com.estoqueti.core.config.DatabaseConfig;
import com.estoqueti.core.exception.DAOException;
import com.estoqueti.core.model.MovimentacaoEstoque;
import com.estoqueti.core.repository.MovimentacaoEstoqueRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementacao JDBC de MovimentacaoEstoqueRepository.
 * Equivalente refatorado de dao.MovimentacaoEstoqueDAO, mas sem o metodo
 * temEstoqueSuficiente() (movido para a service, ver interface) e sem a
 * logica de transacao "inserir movimentacao + atualizar produto" dentro do
 * proprio DAO — essa orquestracao agora fica em
 * service.MovimentacaoEstoqueService, que usa os dois repositorios
 * (Produto e Movimentacao) dentro de uma unica transacao JDBC.
 */
public class MovimentacaoEstoqueRepositoryJDBC implements MovimentacaoEstoqueRepository {

    @Override
    public List<MovimentacaoEstoque> listarTodas() {
        return executarListagem("SELECT * FROM movimentacao_estoque ORDER BY data_movimentacao DESC", null, null);
    }

    @Override
    public List<MovimentacaoEstoque> listarPorTipo(String tipo) {
        return executarListagem(
                "SELECT * FROM movimentacao_estoque WHERE tipo_movimentacao = ? ORDER BY data_movimentacao DESC",
                "S", tipo);
    }

    @Override
    public List<MovimentacaoEstoque> listarPorProduto(int idProduto) {
        return executarListagem(
                "SELECT * FROM movimentacao_estoque WHERE id_produto = ? ORDER BY data_movimentacao DESC",
                "I", String.valueOf(idProduto));
    }

    @Override
    public List<MovimentacaoEstoque> listarUltimas(int limite) {
        return executarListagem(
                "SELECT * FROM movimentacao_estoque ORDER BY data_movimentacao DESC LIMIT ?",
                "I", String.valueOf(limite));
    }

    private List<MovimentacaoEstoque> executarListagem(String sql, String tipoParametro, String valorParametro) {
        List<MovimentacaoEstoque> lista = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            if ("S".equals(tipoParametro)) st.setString(1, valorParametro);
            if ("I".equals(tipoParametro)) st.setInt(1, Integer.parseInt(valorParametro));
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new DAOException("Erro ao listar movimentacoes.", e);
        }
    }

    @Override
    public int contarHoje() {
        String sql = "SELECT COUNT(*) FROM movimentacao_estoque WHERE DATE(data_movimentacao) = CURDATE()";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement st = conn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new DAOException("Erro ao contar movimentacoes de hoje.", e);
        }
    }

    @Override
    public void registrar(MovimentacaoEstoque mov) {
        String sql = "INSERT INTO movimentacao_estoque " +
                "(id_produto, data_movimentacao, tipo_movimentacao, quantidade, observacao, id_usuario) " +
                "VALUES (?, NOW(), ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, mov.getIdProduto());
            st.setString(2, mov.getTipoMovimentacao());
            st.setInt(3, mov.getQuantidade());
            st.setString(4, mov.getObservacao());
            st.setInt(5, mov.getIdUsuario());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erro ao registrar movimentacao.", e);
        }
    }

    private MovimentacaoEstoque mapear(ResultSet rs) throws SQLException {
        MovimentacaoEstoque m = new MovimentacaoEstoque();
        m.setIdMovimentacao(rs.getInt("id_movimentacao"));
        m.setIdProduto(rs.getInt("id_produto"));
        m.setTipoMovimentacao(rs.getString("tipo_movimentacao"));
        m.setQuantidade(rs.getInt("quantidade"));
        m.setObservacao(rs.getString("observacao"));
        m.setIdUsuario(rs.getInt("id_usuario"));
        Timestamp ts = rs.getTimestamp("data_movimentacao");
        if (ts != null) m.setDataMovimentacao(ts.toLocalDateTime());
        return m;
    }
}
