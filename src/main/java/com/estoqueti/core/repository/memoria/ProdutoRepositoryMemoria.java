package com.estoqueti.core.repository.memoria;

import com.estoqueti.core.model.Produto;
import com.estoqueti.core.repository.ProdutoRepository;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementacao em memoria de ProdutoRepository.
 *
 * Existe para dois propositos:
 *  1) Permitir rodar os testes sem precisar de um banco configurado.
 *  2) Demonstrar, na pratica, o Dependency Inversion Principle e o Open/Closed
 *     Principle: ProdutoService so conhece a interface ProdutoRepository.
 *     Trocar esta implementacao pela ProdutoRepositoryJDBC ou pela
 *     ProdutoRepositoryImpl (JPA, Etapa 9) nao exige alterar uma linha
 *     sequer da camada de servico.
 */
public class ProdutoRepositoryMemoria implements ProdutoRepository {

    private final Map<Integer, Produto> tabela = new LinkedHashMap<>();
    private final AtomicInteger proximoId = new AtomicInteger(1);

    @Override
    public List<Produto> listarTodos() {
        return new ArrayList<>(tabela.values());
    }

    @Override
    public Optional<Produto> buscarPorId(int idProduto) {
        return Optional.ofNullable(tabela.get(idProduto));
    }

    @Override
    public Produto salvar(Produto produto) {
        produto.setIdProduto(proximoId.getAndIncrement());
        tabela.put(produto.getIdProduto(), produto);
        return produto;
    }

    @Override
    public void atualizar(Produto produto) {
        tabela.put(produto.getIdProduto(), produto);
    }

    @Override
    public void atualizarQuantidade(int idProduto, int novaQuantidade) {
        Produto p = tabela.get(idProduto);
        if (p != null) {
            p.setQuantidade(novaQuantidade);
        }
    }

    @Override
    public void remover(int idProduto) {
        tabela.remove(idProduto);
    }
}
