package com.estoqueti.web.service;

import com.estoqueti.core.exception.RegraNegocioException;
import com.estoqueti.core.model.Usuario;
import com.estoqueti.core.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UsuarioServiceTest {

    @Autowired private UsuarioService usuarioService;

    private Usuario usuarioValido() {
        return new Usuario(0, "Bruna Lima", "bruna.lima", "123456", "Operador");
    }

    @Test
    void deveCadastrarUsuarioValido() {
        Usuario salvo = usuarioService.cadastrar(usuarioValido());
        assertTrue(salvo.getIdUsuario() > 0);
    }

    @Test
    void deveRejeitarSenhaCurta() {
        Usuario invalido = usuarioValido();
        invalido.setSenha("123");
        assertThrows(RegraNegocioException.class, () -> usuarioService.cadastrar(invalido));
    }

    @Test
    void deveRejeitarLoginDuplicado() {
        usuarioService.cadastrar(usuarioValido());
        Usuario duplicado = usuarioValido();
        duplicado.setNomeUsuario("Outra Pessoa");

        assertThrows(RegraNegocioException.class, () -> usuarioService.cadastrar(duplicado));
    }

    @Test
    void deveAtualizarMantendoSenhaQuandoCampoVazio() {
        Usuario salvo = usuarioService.cadastrar(usuarioValido());

        Usuario dadosEdicao = new Usuario();
        dadosEdicao.setNomeUsuario("Bruna Lima Souza");
        dadosEdicao.setLogin("bruna.lima");
        dadosEdicao.setNivelAcesso("Operador");
        dadosEdicao.setSenha(""); // em branco -> mantem a senha atual

        Usuario atualizado = usuarioService.atualizar(salvo.getIdUsuario(), dadosEdicao);

        assertEquals("Bruna Lima Souza", atualizado.getNomeUsuario());
        assertEquals("123456", atualizado.getSenha());
    }

    @Test
    void deveRejeitarAtualizacaoParaLoginJaUsadoPorOutroUsuario() {
        usuarioService.cadastrar(usuarioValido());
        Usuario outro = usuarioService.cadastrar(new Usuario(0, "Marcos Alves", "marcos.alves", "123456", "Consulta"));

        Usuario dadosEdicao = new Usuario();
        dadosEdicao.setNomeUsuario("Marcos Alves");
        dadosEdicao.setLogin("bruna.lima"); // login de outro usuario
        dadosEdicao.setNivelAcesso("Consulta");

        assertThrows(RegraNegocioException.class, () -> usuarioService.atualizar(outro.getIdUsuario(), dadosEdicao));
    }

    @Test
    void deveRemoverUsuarioExistente() {
        Usuario salvo = usuarioService.cadastrar(usuarioValido());
        usuarioService.remover(salvo.getIdUsuario());
        assertTrue(usuarioService.buscarPorId(salvo.getIdUsuario()).isEmpty());
    }
}
