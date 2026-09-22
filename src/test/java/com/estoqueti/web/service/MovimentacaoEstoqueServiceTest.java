package com.estoqueti.web.service;

import com.estoqueti.core.exception.RegraNegocioException;
import com.estoqueti.core.model.Categoria;
import com.estoqueti.core.model.MovimentacaoEstoque;
import com.estoqueti.core.model.Produto;
import com.estoqueti.core.model.Usuario;
import com.estoqueti.core.repository.CategoriaRepository;
import com.estoqueti.core.service.MovimentacaoEstoqueService;
import com.estoqueti.core.service.ProdutoService;
import com.estoqueti.core.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de MovimentacaoEstoqueService - cobre exatamente as duas regras
 * de negocio centrais desta classe (permissao do usuario e estoque
 * suficiente para saida), que sao as mesmas regras usadas no plano de
 * teste da Etapa 7 e reaproveitadas/expandidas aqui contra o banco real.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MovimentacaoEstoqueServiceTest {

    @Autowired private MovimentacaoEstoqueService movimentacaoService;
    @Autowired private ProdutoService produtoService;
    @Autowired private UsuarioService usuarioService;
    @Autowired private CategoriaRepository categoriaRepository;

    private Produto produto;
    private Usuario administrador;
    private Usuario consulta;

    @BeforeEach
    void setUp() {
        Categoria categoria = categoriaRepository.salvar(new Categoria(0, "Notebooks"));
        produto = produtoService.cadastrar(new Produto(0, "Notebook Dell", "i5/8GB", "UN",
                categoria.getIdCategoria(), "Dell", new BigDecimal("2000.00"), new BigDecimal("2800.00"), 10, "Ativo"));

        administrador = usuarioService.cadastrar(new Usuario(0, "Admin Teste", "admin.teste", "123456", "Administrador"));
        consulta = usuarioService.cadastrar(new Usuario(0, "Consulta Teste", "consulta.teste", "123456", "Consulta"));
    }

    @Test
    void deveRegistrarEntradaEAumentarEstoque() {
        movimentacaoService.registrar(administrador, produto.getIdProduto(), "Entrada", 5, "Recebimento");

        Produto atualizado = produtoService.buscarPorId(produto.getIdProduto()).orElseThrow();
        assertEquals(15, atualizado.getQuantidade());
    }

    @Test
    void deveRegistrarSaidaEDiminuirEstoque() {
        movimentacaoService.registrar(administrador, produto.getIdProduto(), "Saida", 4, "Requisicao");

        Produto atualizado = produtoService.buscarPorId(produto.getIdProduto()).orElseThrow();
        assertEquals(6, atualizado.getQuantidade());
    }

    @Test
    void deveRejeitarSaidaComEstoqueInsuficiente() {
        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () ->
                movimentacaoService.registrar(administrador, produto.getIdProduto(), "Saida", 999, "Requisicao grande"));

        assertTrue(ex.getMessage().contains("insuficiente"));
        // Garante que o estoque NAO foi alterado quando a operacao e rejeitada.
        Produto inalterado = produtoService.buscarPorId(produto.getIdProduto()).orElseThrow();
        assertEquals(10, inalterado.getQuantidade());
    }

    @Test
    void deveRejeitarMovimentacaoDeUsuarioSemPermissao() {
        assertThrows(RegraNegocioException.class, () ->
                movimentacaoService.registrar(consulta, produto.getIdProduto(), "Entrada", 1, "Teste"));
    }

    @Test
    void deveRejeitarQuantidadeZeroOuNegativa() {
        assertThrows(RegraNegocioException.class, () ->
                movimentacaoService.registrar(administrador, produto.getIdProduto(), "Entrada", 0, "Teste"));
    }

    @Test
    void deveRejeitarProdutoInexistente() {
        assertThrows(RegraNegocioException.class, () ->
                movimentacaoService.registrar(administrador, 9999, "Entrada", 1, "Teste"));
    }

    @Test
    void movimentacaoRegistradaDeveAparecerNaListagem() {
        movimentacaoService.registrar(administrador, produto.getIdProduto(), "Entrada", 3, "Teste listagem");

        boolean encontrada = movimentacaoService.listarTodas().stream()
                .anyMatch(m -> "Teste listagem".equals(m.getObservacao()));
        assertTrue(encontrada);
    }
}
