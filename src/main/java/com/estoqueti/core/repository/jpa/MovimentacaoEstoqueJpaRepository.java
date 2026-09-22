package com.estoqueti.core.repository.jpa;

import com.estoqueti.core.model.MovimentacaoEstoque;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MovimentacaoEstoqueJpaRepository extends JpaRepository<MovimentacaoEstoque, Integer> {
    List<MovimentacaoEstoque> findByTipoMovimentacao(String tipo);
    List<MovimentacaoEstoque> findByIdProduto(int idProduto);
    List<MovimentacaoEstoque> findAllByOrderByDataMovimentacaoDesc();
    long countByDataMovimentacaoBetween(LocalDateTime inicio, LocalDateTime fim);
}
