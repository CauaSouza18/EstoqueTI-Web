package com.estoqueti.core.repository;

import com.estoqueti.core.model.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository {
    List<Usuario> listarTodos();

    /** Adicionado na Etapa 9: necessario para a tela de edicao de usuario. */
    Optional<Usuario> buscarPorId(int idUsuario);

    Optional<Usuario> buscarPorLogin(String login);
    Usuario salvar(Usuario usuario);
    void atualizar(Usuario usuario);

    /** Adicionado na Etapa 9: a tela web de usuarios (Etapa 8, restrita a Administrador) permite excluir. */
    void remover(int idUsuario);

    /** Adicionado na Etapa 9: suporte a validacao de login duplicado ao editar um usuario existente. */
    boolean existeOutroComMesmoLogin(String login, int idUsuarioAtual);
}
