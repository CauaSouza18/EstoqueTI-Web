package com.estoqueti.core.repository;

import com.estoqueti.core.model.Categoria;
import java.util.List;
import java.util.Optional;

public interface CategoriaRepository {
    List<Categoria> listarTodas();
    Optional<Categoria> buscarPorId(int idCategoria);
    Categoria salvar(Categoria categoria);

    /** Adicionado na Etapa 9: a tela web de categorias (Etapa 8) permite editar o nome. */
    void atualizar(Categoria categoria);

    /** Adicionado na Etapa 9: a tela web de categorias (Etapa 8) permite excluir uma categoria. */
    void remover(int idCategoria);
}
