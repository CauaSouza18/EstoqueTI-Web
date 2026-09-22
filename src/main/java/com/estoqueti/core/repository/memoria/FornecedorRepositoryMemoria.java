package com.estoqueti.core.repository.memoria;

import com.estoqueti.core.model.Fornecedor;
import com.estoqueti.core.repository.FornecedorRepository;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class FornecedorRepositoryMemoria implements FornecedorRepository {

    private final Map<Integer, Fornecedor> tabela = new LinkedHashMap<>();
    private final AtomicInteger proximoId = new AtomicInteger(1);

    @Override
    public List<Fornecedor> listarTodos() {
        return new ArrayList<>(tabela.values());
    }

    @Override
    public Optional<Fornecedor> buscarPorId(int idFornecedor) {
        return Optional.ofNullable(tabela.get(idFornecedor));
    }

    @Override
    public Fornecedor salvar(Fornecedor fornecedor) {
        fornecedor.setIdFornecedor(proximoId.getAndIncrement());
        tabela.put(fornecedor.getIdFornecedor(), fornecedor);
        return fornecedor;
    }

    @Override
    public void atualizar(Fornecedor fornecedor) {
        tabela.put(fornecedor.getIdFornecedor(), fornecedor);
    }

    @Override
    public void remover(int idFornecedor) {
        tabela.remove(idFornecedor);
    }
}
