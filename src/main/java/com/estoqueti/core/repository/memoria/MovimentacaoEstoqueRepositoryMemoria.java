package com.estoqueti.core.repository.memoria;

import com.estoqueti.core.model.MovimentacaoEstoque;
import com.estoqueti.core.repository.MovimentacaoEstoqueRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MovimentacaoEstoqueRepositoryMemoria implements MovimentacaoEstoqueRepository {

    private final List<MovimentacaoEstoque> tabela = new ArrayList<>();
    private final AtomicInteger proximoId = new AtomicInteger(1);

    @Override
    public List<MovimentacaoEstoque> listarTodas() {
        List<MovimentacaoEstoque> copia = new ArrayList<>(tabela);
        copia.sort(Comparator.comparing(MovimentacaoEstoque::getDataMovimentacao).reversed());
        return copia;
    }

    @Override
    public List<MovimentacaoEstoque> listarPorTipo(String tipo) {
        return listarTodas().stream()
                .filter(m -> tipo.equals(m.getTipoMovimentacao()))
                .collect(Collectors.toList());
    }

    @Override
    public List<MovimentacaoEstoque> listarPorProduto(int idProduto) {
        return listarTodas().stream()
                .filter(m -> m.getIdProduto() == idProduto)
                .collect(Collectors.toList());
    }

    @Override
    public List<MovimentacaoEstoque> listarUltimas(int limite) {
        return listarTodas().stream().limit(limite).collect(Collectors.toList());
    }

    @Override
    public int contarHoje() {
        LocalDate hoje = LocalDate.now();
        return (int) tabela.stream()
                .filter(m -> m.getDataMovimentacao().toLocalDate().equals(hoje))
                .count();
    }

    @Override
    public void registrar(MovimentacaoEstoque movimentacao) {
        movimentacao.setIdMovimentacao(proximoId.getAndIncrement());
        tabela.add(movimentacao);
    }
}
