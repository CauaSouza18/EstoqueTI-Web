package com.estoqueti.core.repository;

import com.estoqueti.core.model.NotaFiscal;
import java.util.List;
import java.util.Optional;

public interface NotaFiscalRepository {
    List<NotaFiscal> listarTodas();

    /** Adicionado na Etapa 9: necessario para pre-carregar o modal de edicao no front-end. */
    Optional<NotaFiscal> buscarPorId(int idNf);

    NotaFiscal salvar(NotaFiscal notaFiscal);
    void atualizar(NotaFiscal notaFiscal);

    /** Adicionado na Etapa 9: a tela web de notas fiscais (Etapa 8) permite excluir. */
    void remover(int idNf);
}
