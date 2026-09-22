package com.estoqueti.core.repository.memoria;

import com.estoqueti.core.model.Usuario;
import com.estoqueti.core.repository.UsuarioRepository;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class UsuarioRepositoryMemoria implements UsuarioRepository {

    private final Map<Integer, Usuario> tabela = new LinkedHashMap<>();
    private final AtomicInteger proximoId = new AtomicInteger(1);

    @Override
    public List<Usuario> listarTodos() {
        return new ArrayList<>(tabela.values());
    }

    @Override
    public Optional<Usuario> buscarPorId(int idUsuario) {
        return Optional.ofNullable(tabela.get(idUsuario));
    }

    @Override
    public Optional<Usuario> buscarPorLogin(String login) {
        return tabela.values().stream()
                .filter(u -> u.getLogin().equalsIgnoreCase(login))
                .findFirst();
    }

    @Override
    public Usuario salvar(Usuario usuario) {
        usuario.setIdUsuario(proximoId.getAndIncrement());
        tabela.put(usuario.getIdUsuario(), usuario);
        return usuario;
    }

    @Override
    public void atualizar(Usuario usuario) {
        tabela.put(usuario.getIdUsuario(), usuario);
    }

    @Override
    public void remover(int idUsuario) {
        tabela.remove(idUsuario);
    }

    @Override
    public boolean existeOutroComMesmoLogin(String login, int idUsuarioAtual) {
        return tabela.values().stream()
                .anyMatch(u -> u.getLogin().equalsIgnoreCase(login) && u.getIdUsuario() != idUsuarioAtual);
    }
}
