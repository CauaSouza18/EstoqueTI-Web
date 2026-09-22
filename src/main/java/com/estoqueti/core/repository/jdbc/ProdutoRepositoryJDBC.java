package com.estoqueti.core.repository.jdbc;

import com.estoqueti.core.config.DatabaseConfig;
import com.estoqueti.core.exception.DAOException;
import com.estoqueti.core.model.Produto;
import com.estoqueti.core.repository.ProdutoRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementacao JDBC/MySQL de ProdutoRepository.
 * Equivalente refatorado de dao.ProdutoDAO, agora:
 *  - implementa a interface ProdutoRepository (permite substituicao / mocks);
 *  - abre e fecha a conexao a cada operacao (try-with-resources), em vez de
 *    manter 1 Connection guardada para sempre no atributo da classe;
 *  - possui o CRUD completo (o original nao tinha update nem buscarPorId);
 *  - propaga falhas como DAOException em vez de imprimir e engolir o erro.
 */
public class ProdutoRepositoryJDBC implements ProdutoRepository {

    @Override
    public List<Produto> listarTodos() {
        String sql = "SELECT * FROM produtos";
        List<Produto> lista = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement st = conn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new DAOException("Erro ao listar produtos.", e);
        }
    }

    @Override
    public Optional<Produto> buscarPorId(int idProduto) {
        String sql = "SELECT * FROM produtos WHERE id_produto = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, idProduto);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao buscar produto id=" + idProduto, e);
        }
    }

    @Override
    public Produto salvar(Produto produto) {
        String sql = "INSERT INTO produtos (nome_produto, descricao, unidade_medida, " +
                "id_categoria, marca, preco_custo, preco_venda, quantidade, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencherParametros(st, produto);
            st.executeUpdate();
            try (ResultSet keys = st.getGeneratedKeys()) {
                if (keys.next()) {
                    produto.setIdProduto(keys.getInt(1));
                }
            }
            return produto;
        } catch (SQLException e) {
            throw new DAOException("Erro ao salvar produto.", e);
        }
    }

    @Override
    public void atualizar(Produto produto) {
        String sql = "UPDATE produtos SET nome_produto=?, descricao=?, unidade_medida=?, " +
                "id_categoria=?, marca=?, preco_custo=?, preco_venda=?, quantidade=?, status=? " +
                "WHERE id_produto=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            preencherParametros(st, produto);
            st.setInt(10, produto.getIdProduto());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erro ao atualizar produto id=" + produto.getIdProduto(), e);
        }
    }

    @Override
    public void atualizarQuantidade(int idProduto, int novaQuantidade) {
        String sql = "UPDATE produtos SET quantidade = ? WHERE id_produto = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, novaQuantidade);
            st.setInt(2, idProduto);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erro ao atualizar quantidade do produto id=" + idProduto, e);
        }
    }

    @Override
    public void remover(int idProduto) {
        String sql = "DELETE FROM produtos WHERE id_produto=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, idProduto);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erro ao remover produto id=" + idProduto, e);
        }
    }

    private void preencherParametros(PreparedStatement st, Produto p) throws SQLException {
        st.setString(1, p.getNomeProduto());
        st.setString(2, p.getDescricao());
        st.setString(3, p.getUnidadeMedida());
        st.setInt(4, p.getIdCategoria());
        st.setString(5, p.getMarca());
        st.setBigDecimal(6, p.getPrecoCusto());
        st.setBigDecimal(7, p.getPrecoVenda());
        st.setInt(8, p.getQuantidade());
        st.setString(9, p.getStatus());
    }

    private Produto mapear(ResultSet rs) throws SQLException {
        Produto p = new Produto();
        p.setIdProduto(rs.getInt("id_produto"));
        p.setNomeProduto(rs.getString("nome_produto"));
        p.setDescricao(rs.getString("descricao"));
        p.setUnidadeMedida(rs.getString("unidade_medida"));
        p.setIdCategoria(rs.getInt("id_categoria"));
        p.setMarca(rs.getString("marca"));
        p.setPrecoCusto(rs.getBigDecimal("preco_custo"));
        p.setPrecoVenda(rs.getBigDecimal("preco_venda"));
        p.setQuantidade(rs.getInt("quantidade"));
        p.setStatus(rs.getString("status"));
        return p;
    }
}
