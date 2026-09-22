package com.estoqueti.core.repository.jpa;

import com.estoqueti.core.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdutoJpaRepository extends JpaRepository<Produto, Integer> {
    List<Produto> findByStatus(String status);
}
