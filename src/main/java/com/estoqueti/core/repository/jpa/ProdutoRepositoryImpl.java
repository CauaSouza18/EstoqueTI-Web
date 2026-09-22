package com.estoqueti.core.repository.jpa;

import com.estoqueti.core.model.Produto;
import com.estoqueti.core.repository.ProdutoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProdutoRepositoryImpl implements ProdutoRepository {

    private final ProdutoJpaRepository jpaRepository;

    public ProdutoRepositoryImpl(ProdutoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Produto> listarTodos() {
        return jpaRepository.findAll();
    }

    @Override
    public Optional<Produto> buscarPorId(int idProduto) {
        return jpaRepository.findById(idProduto);
    }

    @Override
    public Produto salvar(Produto produto) {
        return jpaRepository.save(produto);
    }

    @Override
    public void atualizar(Produto produto) {
        jpaRepository.save(produto);
    }

    @Override
    public void atualizarQuantidade(int idProduto, int novaQuantidade) {
        jpaRepository.findById(idProduto).ifPresent(p -> {
            p.setQuantidade(novaQuantidade);
            jpaRepository.save(p);
        });
    }

    @Override
    public void remover(int idProduto) {
        jpaRepository.deleteById(idProduto);
    }
}
