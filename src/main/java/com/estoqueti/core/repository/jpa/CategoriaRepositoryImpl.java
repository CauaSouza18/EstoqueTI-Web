package com.estoqueti.core.repository.jpa;

import com.estoqueti.core.model.Categoria;
import com.estoqueti.core.repository.CategoriaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador que liga a interface CategoriaRepository (Etapa 6, inalterada)
 * ao Spring Data JPA (Etapa 9). CategoriaService continua dependendo apenas
 * da interface CategoriaRepository - nao sabe que por baixo existe JPA/H2.
 */
@Repository
public class CategoriaRepositoryImpl implements CategoriaRepository {

    private final CategoriaJpaRepository jpaRepository;

    public CategoriaRepositoryImpl(CategoriaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Categoria> listarTodas() {
        return jpaRepository.findAll();
    }

    @Override
    public Optional<Categoria> buscarPorId(int idCategoria) {
        return jpaRepository.findById(idCategoria);
    }

    @Override
    public Categoria salvar(Categoria categoria) {
        return jpaRepository.save(categoria);
    }

    @Override
    public void atualizar(Categoria categoria) {
        jpaRepository.save(categoria);
    }

    @Override
    public void remover(int idCategoria) {
        jpaRepository.deleteById(idCategoria);
    }
}
