package com.estoqueti.core.service;

import com.estoqueti.core.exception.RegraNegocioException;
import com.estoqueti.core.model.Usuario;
import com.estoqueti.core.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Regras de negocio de Usuario (CRUD administrativo).
 *
 * Classe nova da Etapa 9: a Etapa 6 so previa AutenticacaoService (login).
 * A tela web "usuarios.html" (Etapa 8, restrita ao perfil Administrador)
 * exige tambem listar, cadastrar, editar e excluir usuarios - por isso esta
 * service foi criada seguindo o mesmo padrao das demais (SRP: valida regras
 * e delega persistencia a UsuarioRepository).
 */
@Service
public class UsuarioService {

    private static final int TAMANHO_MINIMO_SENHA = 6;

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.listarTodos();
    }

    public Optional<Usuario> buscarPorId(int idUsuario) {
        return usuarioRepository.buscarPorId(idUsuario);
    }

    public Usuario cadastrar(Usuario usuario) {
        validarCamposObrigatorios(usuario);
        validarSenha(usuario.getSenha());
        if (usuarioRepository.buscarPorLogin(usuario.getLogin()).isPresent()) {
            throw new RegraNegocioException("Ja existe um usuario com este login.");
        }
        return usuarioRepository.salvar(usuario);
    }

    /**
     * Atualiza um usuario existente. Se a senha vier em branco, mantem a
     * senha anterior (assim o formulario de edicao no front-end pode deixar
     * o campo de senha vazio quando o operador nao quiser altera-la).
     */
    public Usuario atualizar(int idUsuario, Usuario dados) {
        Usuario existente = usuarioRepository.buscarPorId(idUsuario)
                .orElseThrow(() -> new RegraNegocioException("Usuario id=" + idUsuario + " nao encontrado."));

        validarCamposObrigatorios(dados);
        if (usuarioRepository.existeOutroComMesmoLogin(dados.getLogin(), idUsuario)) {
            throw new RegraNegocioException("Ja existe outro usuario com este login.");
        }

        existente.setNomeUsuario(dados.getNomeUsuario());
        existente.setLogin(dados.getLogin());
        existente.setNivelAcesso(dados.getNivelAcesso());
        if (dados.getSenha() != null && !dados.getSenha().isBlank()) {
            validarSenha(dados.getSenha());
            existente.setSenha(dados.getSenha());
        }

        usuarioRepository.atualizar(existente);
        return existente;
    }

    public void remover(int idUsuario) {
        usuarioRepository.buscarPorId(idUsuario)
                .orElseThrow(() -> new RegraNegocioException("Usuario id=" + idUsuario + " nao encontrado."));
        usuarioRepository.remover(idUsuario);
    }

    private void validarCamposObrigatorios(Usuario usuario) {
        if (usuario.getNomeUsuario() == null || usuario.getNomeUsuario().trim().isEmpty()) {
            throw new RegraNegocioException("nomeUsuario e obrigatorio.");
        }
        if (usuario.getLogin() == null || usuario.getLogin().trim().isEmpty()) {
            throw new RegraNegocioException("login e obrigatorio.");
        }
        if (usuario.getNivelAcesso() == null || usuario.getNivelAcesso().trim().isEmpty()) {
            throw new RegraNegocioException("nivelAcesso e obrigatorio.");
        }
    }

    private void validarSenha(String senha) {
        if (senha == null || senha.length() < TAMANHO_MINIMO_SENHA) {
            throw new RegraNegocioException("A senha deve ter ao menos " + TAMANHO_MINIMO_SENHA + " caracteres.");
        }
    }
}
