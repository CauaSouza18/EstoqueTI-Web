package com.estoqueti.core.repository.jpa;

import com.estoqueti.core.model.Usuario;
import com.estoqueti.core.repository.UsuarioRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UsuarioRepositoryImpl implements UsuarioRepository {

    private final UsuarioJpaRepository jpaRepository;

    public UsuarioRepositoryImpl(UsuarioJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Usuario> listarTodos() {
        return jpaRepository.findAll();
    }

    @Override
    public Optional<Usuario> buscarPorId(int idUsuario) {
        return jpaRepository.findById(idUsuario);
    }

    @Override
    public Optional<Usuario> buscarPorLogin(String login) {
        return jpaRepository.findByLoginIgnoreCase(login);
    }

    @Override
    public Usuario salvar(Usuario usuario) {
        return jpaRepository.save(usuario);
    }

    @Override
    public void atualizar(Usuario usuario) {
        jpaRepository.save(usuario);
    }

    @Override
    public void remover(int idUsuario) {
        jpaRepository.deleteById(idUsuario);
    }

    @Override
    public boolean existeOutroComMesmoLogin(String login, int idUsuarioAtual) {
        return jpaRepository.existsByLoginIgnoreCaseAndIdUsuarioNot(login, idUsuarioAtual);
    }
}
