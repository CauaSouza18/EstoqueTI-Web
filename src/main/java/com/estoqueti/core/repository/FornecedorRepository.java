package com.estoqueti.core.repository;

import com.estoqueti.core.model.Fornecedor;
import java.util.List;
import java.util.Optional;

public interface FornecedorRepository {
    List<Fornecedor> listarTodos();
    Optional<Fornecedor> buscarPorId(int idFornecedor);
    Fornecedor salvar(Fornecedor fornecedor);
    void atualizar(Fornecedor fornecedor);

    /** Adicionado na Etapa 9: a tela web de fornecedores (Etapa 8) permite excluir. */
    void remover(int idFornecedor);
}
