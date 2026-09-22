package com.estoqueti.web.service;

import com.estoqueti.core.exception.RegraNegocioException;
import com.estoqueti.core.model.Usuario;
import com.estoqueti.core.service.AutenticacaoService;
import com.estoqueti.core.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AutenticacaoServiceTest {

    @Autowired private AutenticacaoService autenticacaoService;
    @Autowired private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        usuarioService.cadastrar(new Usuario(0, "Caua Souza", "cana.souza", "123456", "Administrador"));
    }

    @Test
    void deveAutenticarComCredenciaisValidas() {
        Usuario usuario = autenticacaoService.autenticar("cana.souza", "123456");
        assertEquals("Caua Souza", usuario.getNomeUsuario());
    }

    @Test
    void deveAutenticarLoginIgnorandoCaixa() {
        Usuario usuario = autenticacaoService.autenticar("CANA.SOUZA", "123456");
        assertEquals("cana.souza", usuario.getLogin());
    }

    @Test
    void deveRejeitarSenhaIncorreta() {
        assertThrows(RegraNegocioException.class, () -> autenticacaoService.autenticar("cana.souza", "errada"));
    }

    @Test
    void deveRejeitarLoginInexistente() {
        assertThrows(RegraNegocioException.class, () -> autenticacaoService.autenticar("nao.existe", "123456"));
    }
}
