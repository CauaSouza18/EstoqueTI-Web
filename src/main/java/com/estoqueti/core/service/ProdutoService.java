package com.estoqueti.core.service;

import com.estoqueti.core.exception.RegraNegocioException;
import com.estoqueti.core.model.Produto;
import com.estoqueti.core.repository.CategoriaRepository;
import com.estoqueti.core.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Regras de negocio de Produto. Logica original da Etapa 6 preservada
 * integralmente (validar/cadastrar/atualizar/calcularMargemLucro);
 * @Service e remover() sao acrescimos da Etapa 9 (a tela web de produtos
 * da Etapa 8 permite excluir um produto).
 */
@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProdutoService(ProdutoRepository produtoRepository, CategoriaRepository categoriaRepository) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public List<Produto> listarTodos() {
        return produtoRepository.listarTodos();
    }

    public Optional<Produto> buscarPorId(int idProduto) {
        return produtoRepository.buscarPorId(idProduto);
    }

    public Produto cadastrar(Produto produto) {
        validar(produto);
        return produtoRepository.salvar(produto);
    }

    public void atualizar(Produto produto) {
        if (produto.getIdProduto() <= 0) {
            throw new RegraNegocioException("Produto invalido para atualizacao (id ausente).");
        }
        validar(produto);
        produtoRepository.atualizar(produto);
    }

    /** Adicionado na Etapa 9. */
    public void remover(int idProduto) {
        produtoRepository.buscarPorId(idProduto)
                .orElseThrow(() -> new RegraNegocioException("Produto id=" + idProduto + " nao encontrado."));
        produtoRepository.remover(idProduto);
    }

    /** Delega ao proprio modelo - nao recalcula nada aqui, evitando duplicacao. */
    public BigDecimal calcularMargemLucro(Produto produto) {
        return produto.calcularMargemLucro();
    }

    private void validar(Produto produto) {
        if (produto.getNomeProduto() == null || produto.getNomeProduto().trim().isEmpty()) {
            throw new RegraNegocioException("nomeProduto e obrigatorio.");
        }
        if (produto.getIdCategoria() <= 0 || categoriaRepository.buscarPorId(produto.getIdCategoria()).isEmpty()) {
            throw new RegraNegocioException("Selecione uma categoria valida.");
        }
        if (produto.getPrecoCusto() == null || produto.getPrecoCusto().compareTo(BigDecimal.ZERO) < 0) {
            throw new RegraNegocioException("Preco de custo invalido.");
        }
        if (produto.getPrecoVenda() == null || produto.getPrecoVenda().compareTo(BigDecimal.ZERO) < 0) {
            throw new RegraNegocioException("Preco de venda invalido.");
        }
        if (produto.getQuantidade() < 0) {
            throw new RegraNegocioException("Quantidade nao pode ser negativa.");
        }
    }
}
