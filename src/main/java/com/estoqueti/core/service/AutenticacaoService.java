package com.estoqueti.core.service;

import com.estoqueti.core.exception.RegraNegocioException;
import com.estoqueti.core.model.Usuario;
import com.estoqueti.core.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

/**
 * Regras de autenticacao, extraidas de view.TelaLogin.
 * A tela original consultava o banco diretamente (via UsuarioDAO) e ainda
 * decidia, dentro do actionListener do botao, qual JOptionPane mostrar
 * para cada tipo de erro. Aqui a decisao "login e senha conferem?" fica
 * isolada e testavel sem precisar abrir nenhuma janela Swing.
 */
@Service
public class AutenticacaoService {

    private final UsuarioRepository usuarioRepository;

    public AutenticacaoService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario autenticar(String login, String senha) {
        Usuario usuario = usuarioRepository.buscarPorLogin(login)
                .orElseThrow(() -> new RegraNegocioException("Login ou senha invalidos."));

        if (!usuario.getSenha().equals(senha)) {
            throw new RegraNegocioException("Login ou senha invalidos.");
        }
        return usuario;
    }
}
