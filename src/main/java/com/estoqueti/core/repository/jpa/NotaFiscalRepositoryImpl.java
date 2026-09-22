package com.estoqueti.core.repository.jpa;

import com.estoqueti.core.model.NotaFiscal;
import com.estoqueti.core.repository.NotaFiscalRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class NotaFiscalRepositoryImpl implements NotaFiscalRepository {

    private final NotaFiscalJpaRepository jpaRepository;

    public NotaFiscalRepositoryImpl(NotaFiscalJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<NotaFiscal> listarTodas() {
        return jpaRepository.findAll();
    }

    @Override
    public Optional<NotaFiscal> buscarPorId(int idNf) {
        return jpaRepository.findById(idNf);
    }

    @Override
    public NotaFiscal salvar(NotaFiscal notaFiscal) {
        return jpaRepository.save(notaFiscal);
    }

    @Override
    public void atualizar(NotaFiscal notaFiscal) {
        jpaRepository.save(notaFiscal);
    }

    @Override
    public void remover(int idNf) {
        jpaRepository.deleteById(idNf);
    }
}
