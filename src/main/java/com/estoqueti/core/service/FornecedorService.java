package com.estoqueti.core.service;

import com.estoqueti.core.exception.RegraNegocioException;
import com.estoqueti.core.model.Fornecedor;
import com.estoqueti.core.repository.FornecedorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Regras de negocio de Fornecedor. Logica original da Etapa 6 preservada;
 * @Service e remover() sao acrescimos da Etapa 9 (a tela web de
 * fornecedores da Etapa 8 permite excluir um cadastro).
 */
@Service
public class FornecedorService {

    private final FornecedorRepository fornecedorRepository;

    public FornecedorService(FornecedorRepository fornecedorRepository) {
        this.fornecedorRepository = fornecedorRepository;
    }

    public List<Fornecedor> listarTodos() {
        return fornecedorRepository.listarTodos();
    }

    public Fornecedor cadastrar(Fornecedor fornecedor) {
        validar(fornecedor);
        return fornecedorRepository.salvar(fornecedor);
    }

    public void atualizar(Fornecedor fornecedor) {
        if (fornecedor.getIdFornecedor() <= 0) {
            throw new RegraNegocioException("Fornecedor invalido para atualizacao (id ausente).");
        }
        validar(fornecedor);
        fornecedorRepository.atualizar(fornecedor);
    }

    /** Adicionado na Etapa 9. */
    public void remover(int idFornecedor) {
        fornecedorRepository.buscarPorId(idFornecedor)
                .orElseThrow(() -> new RegraNegocioException("Fornecedor id=" + idFornecedor + " nao encontrado."));
        fornecedorRepository.remover(idFornecedor);
    }

    private void validar(Fornecedor f) {
        if (f.getNomeFornecedor() == null || f.getNomeFornecedor().trim().isEmpty()) {
            throw new RegraNegocioException("nomeFornecedor e obrigatorio.");
        }
        if (f.getCnpj() == null || f.getCnpj().trim().isEmpty()) {
            throw new RegraNegocioException("CNPJ e obrigatorio.");
        }
    }
}
