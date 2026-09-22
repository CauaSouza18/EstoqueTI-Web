package com.estoqueti.core.repository.jpa;

import com.estoqueti.core.model.MovimentacaoEstoque;
import com.estoqueti.core.repository.MovimentacaoEstoqueRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public class MovimentacaoEstoqueRepositoryImpl implements MovimentacaoEstoqueRepository {

    private final MovimentacaoEstoqueJpaRepository jpaRepository;

    public MovimentacaoEstoqueRepositoryImpl(MovimentacaoEstoqueJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<MovimentacaoEstoque> listarTodas() {
        return jpaRepository.findAllByOrderByDataMovimentacaoDesc();
    }

    @Override
    public List<MovimentacaoEstoque> listarPorTipo(String tipo) {
        return jpaRepository.findByTipoMovimentacao(tipo);
    }

    @Override
    public List<MovimentacaoEstoque> listarPorProduto(int idProduto) {
        return jpaRepository.findByIdProduto(idProduto);
    }

    @Override
    public List<MovimentacaoEstoque> listarUltimas(int limite) {
        List<MovimentacaoEstoque> todas = jpaRepository.findAllByOrderByDataMovimentacaoDesc();
        return todas.size() > limite ? todas.subList(0, limite) : todas;
    }

    @Override
    public int contarHoje() {
        LocalDate hoje = LocalDate.now();
        long total = jpaRepository.countByDataMovimentacaoBetween(
                hoje.atStartOfDay(), hoje.atTime(LocalTime.MAX));
        return (int) total;
    }

    @Override
    public void registrar(MovimentacaoEstoque movimentacao) {
        jpaRepository.save(movimentacao);
    }
}
