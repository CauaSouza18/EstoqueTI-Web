package com.estoqueti.core.repository.memoria;

import com.estoqueti.core.model.Categoria;
import com.estoqueti.core.repository.CategoriaRepository;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CategoriaRepositoryMemoria implements CategoriaRepository {

    private final Map<Integer, Categoria> tabela = new LinkedHashMap<>();
    private final AtomicInteger proximoId = new AtomicInteger(1);

    @Override
    public List<Categoria> listarTodas() {
        return new ArrayList<>(tabela.values());
    }

    @Override
    public Optional<Categoria> buscarPorId(int idCategoria) {
        return Optional.ofNullable(tabela.get(idCategoria));
    }

    @Override
    public Categoria salvar(Categoria categoria) {
        categoria.setIdCategoria(proximoId.getAndIncrement());
        tabela.put(categoria.getIdCategoria(), categoria);
        return categoria;
    }

    @Override
    public void atualizar(Categoria categoria) {
        tabela.put(categoria.getIdCategoria(), categoria);
    }

    @Override
    public void remover(int idCategoria) {
        tabela.remove(idCategoria);
    }
}
