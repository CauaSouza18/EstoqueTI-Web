package com.estoqueti.core.repository.memoria;

import com.estoqueti.core.model.NotaFiscal;
import com.estoqueti.core.repository.NotaFiscalRepository;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class NotaFiscalRepositoryMemoria implements NotaFiscalRepository {

    private final Map<Integer, NotaFiscal> tabela = new LinkedHashMap<>();
    private final AtomicInteger proximoId = new AtomicInteger(1);

    @Override
    public List<NotaFiscal> listarTodas() {
        return new ArrayList<>(tabela.values());
    }

    @Override
    public Optional<NotaFiscal> buscarPorId(int idNf) {
        return Optional.ofNullable(tabela.get(idNf));
    }

    @Override
    public NotaFiscal salvar(NotaFiscal notaFiscal) {
        notaFiscal.setIdNf(proximoId.getAndIncrement());
        tabela.put(notaFiscal.getIdNf(), notaFiscal);
        return notaFiscal;
    }

    @Override
    public void atualizar(NotaFiscal notaFiscal) {
        tabela.put(notaFiscal.getIdNf(), notaFiscal);
    }

    @Override
    public void remover(int idNf) {
        tabela.remove(idNf);
    }
}
