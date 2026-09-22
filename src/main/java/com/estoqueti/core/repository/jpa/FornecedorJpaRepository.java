package com.estoqueti.core.repository.jpa;

import com.estoqueti.core.model.Fornecedor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FornecedorJpaRepository extends JpaRepository<Fornecedor, Integer> {
}
