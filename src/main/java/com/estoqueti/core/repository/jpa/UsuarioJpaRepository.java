package com.estoqueti.core.repository.jpa;

import com.estoqueti.core.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioJpaRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByLoginIgnoreCase(String login);
    boolean existsByLoginIgnoreCaseAndIdUsuarioNot(String login, int idUsuario);
}
