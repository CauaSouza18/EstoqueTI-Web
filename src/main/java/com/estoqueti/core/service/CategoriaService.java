package com.estoqueti.core.service;

import com.estoqueti.core.exception.RegraNegocioException;
import com.estoqueti.core.model.Categoria;
import com.estoqueti.core.repository.CategoriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Regras de negocio de Categoria. Copiada da Etapa 6 sem alteracoes na
 * logica original; @Service e os metodos atualizar()/remover() sao
 * acrescimos da Etapa 9 para atender a tela web de categorias (Etapa 8),
 * que permite editar e excluir - operacoes que a Etapa 6 ainda nao previa.
 */
@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<Categoria> listarTodas() {
        return categoriaRepository.listarTodas();
    }

    public Categoria cadastrar(String nomeCategoria) {
        validarNome(nomeCategoria);
        return categoriaRepository.salvar(new Categoria(0, nomeCategoria.trim()));
    }

    /** Adicionado na Etapa 9. */
    public Categoria atualizar(int idCategoria, String nomeCategoria) {
        validarNome(nomeCategoria);
        Categoria categoria = categoriaRepository.buscarPorId(idCategoria)
                .orElseThrow(() -> new RegraNegocioException("Categoria id=" + idCategoria + " nao encontrada."));
        categoria.setNomeCategoria(nomeCategoria.trim());
        categoriaRepository.atualizar(categoria);
        return categoria;
    }

    /** Adicionado na Etapa 9. */
    public void remover(int idCategoria) {
        categoriaRepository.buscarPorId(idCategoria)
                .orElseThrow(() -> new RegraNegocioException("Categoria id=" + idCategoria + " nao encontrada."));
        categoriaRepository.remover(idCategoria);
    }

    private void validarNome(String nomeCategoria) {
        if (nomeCategoria == null || nomeCategoria.trim().isEmpty()) {
            throw new RegraNegocioException("O nome da categoria e obrigatorio.");
        }
    }
}
