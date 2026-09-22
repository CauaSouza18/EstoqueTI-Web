package com.estoqueti.core.repository.jpa;

import com.estoqueti.core.model.Fornecedor;
import com.estoqueti.core.repository.FornecedorRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class FornecedorRepositoryImpl implements FornecedorRepository {

    private final FornecedorJpaRepository jpaRepository;

    public FornecedorRepositoryImpl(FornecedorJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Fornecedor> listarTodos() {
        return jpaRepository.findAll();
    }

    @Override
    public Optional<Fornecedor> buscarPorId(int idFornecedor) {
        return jpaRepository.findById(idFornecedor);
    }

    @Override
    public Fornecedor salvar(Fornecedor fornecedor) {
        return jpaRepository.save(fornecedor);
    }

    @Override
    public void atualizar(Fornecedor fornecedor) {
        jpaRepository.save(fornecedor);
    }

    @Override
    public void remover(int idFornecedor) {
        jpaRepository.deleteById(idFornecedor);
    }
}
