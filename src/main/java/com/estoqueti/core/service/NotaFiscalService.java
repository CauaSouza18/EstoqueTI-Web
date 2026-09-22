package com.estoqueti.core.service;

import com.estoqueti.core.exception.RegraNegocioException;
import com.estoqueti.core.model.NotaFiscal;
import com.estoqueti.core.repository.FornecedorRepository;
import com.estoqueti.core.repository.NotaFiscalRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Regras de negocio de Nota Fiscal. cadastrar()/validar() preservados
 * da Etapa 6; buscarPorId()/atualizar()/remover() sao acrescimos da
 * Etapa 9 (a tela web de notas fiscais da Etapa 8 permite editar e excluir).
 */
@Service
public class NotaFiscalService {

    private final NotaFiscalRepository notaFiscalRepository;
    private final FornecedorRepository fornecedorRepository;

    public NotaFiscalService(NotaFiscalRepository notaFiscalRepository, FornecedorRepository fornecedorRepository) {
        this.notaFiscalRepository = notaFiscalRepository;
        this.fornecedorRepository = fornecedorRepository;
    }

    public List<NotaFiscal> listarTodas() {
        return notaFiscalRepository.listarTodas();
    }

    /** Adicionado na Etapa 9. */
    public Optional<NotaFiscal> buscarPorId(int idNf) {
        return notaFiscalRepository.buscarPorId(idNf);
    }

    public NotaFiscal cadastrar(NotaFiscal notaFiscal) {
        validar(notaFiscal);
        return notaFiscalRepository.salvar(notaFiscal);
    }

    /** Adicionado na Etapa 9. */
    public void atualizar(NotaFiscal notaFiscal) {
        if (notaFiscal.getIdNf() <= 0) {
            throw new RegraNegocioException("Nota fiscal invalida para atualizacao (id ausente).");
        }
        validar(notaFiscal);
        notaFiscalRepository.atualizar(notaFiscal);
    }

    /** Adicionado na Etapa 9. */
    public void remover(int idNf) {
        notaFiscalRepository.buscarPorId(idNf)
                .orElseThrow(() -> new RegraNegocioException("Nota fiscal id=" + idNf + " nao encontrada."));
        notaFiscalRepository.remover(idNf);
    }

    private void validar(NotaFiscal nf) {
        if (nf.getNumeroNf() == null || nf.getNumeroNf().trim().isEmpty()) {
            throw new RegraNegocioException("numeroNf e obrigatorio.");
        }
        if (nf.getValorTotal() == null || nf.getValorTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("valorTotal deve ser maior que zero.");
        }
        if (fornecedorRepository.buscarPorId(nf.getIdFornecedor()).isEmpty()) {
            throw new RegraNegocioException("Fornecedor da nota fiscal nao encontrado.");
        }
    }
}
