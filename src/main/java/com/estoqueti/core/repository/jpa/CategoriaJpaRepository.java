package com.estoqueti.core.repository.jpa;

import com.estoqueti.core.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repositorio Spring Data JPA para Categoria. Spring gera a implementacao em tempo de execucao. */
public interface CategoriaJpaRepository extends JpaRepository<Categoria, Integer> {
}
