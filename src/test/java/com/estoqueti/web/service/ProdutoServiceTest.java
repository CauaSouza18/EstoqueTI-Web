package com.estoqueti.web.service;

import com.estoqueti.core.exception.RegraNegocioException;
import com.estoqueti.core.model.Categoria;
import com.estoqueti.core.model.Produto;
import com.estoqueti.core.repository.CategoriaRepository;
import com.estoqueti.core.service.ProdutoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de ProdutoService rodando contra um banco H2 real (Spring Data
 * JPA), nao contra a implementacao em memoria - valida de ponta a ponta
 * que a integracao Service -> Repository -> JPA -> banco funciona, que era
 * exatamente o objetivo da Etapa 9.
 *
 * @Transactional em cada teste garante rollback automatico ao final,
 * mantendo os testes isolados uns dos outros.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProdutoServiceTest {

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private CategoriaRepository categoriaRepository;

    private int idCategoriaValida;

    @BeforeEach
    void setUp() {
        Categoria categoria = categoriaRepository.salvar(new Categoria(0, "Perifericos"));
        idCategoriaValida = categoria.getIdCategoria();
    }

    private Produto produtoValido() {
        return new Produto(0, "Mouse Optico", "Mouse com fio", "UN", idCategoriaValida,
                "Logitech", new BigDecimal("18.50"), new BigDecimal("39.90"), 10, "Ativo");
    }

    @Test
    void deveCadastrarProdutoValido() {
        Produto salvo = produtoService.cadastrar(produtoValido());

        assertTrue(salvo.getIdProduto() > 0);
        List<Produto> todos = produtoService.listarTodos();
        assertEquals(1, todos.size());
        assertEquals("Mouse Optico", todos.get(0).getNomeProduto());
    }

    @Test
    void deveRejeitarProdutoSemNome() {
        Produto invalido = produtoValido();
        invalido.setNomeProduto("  ");

        assertThrows(RegraNegocioException.class, () -> produtoService.cadastrar(invalido));
    }

    @Test
    void deveRejeitarProdutoComCategoriaInexistente() {
        Produto invalido = produtoValido();
        invalido.setIdCategoria(9999);

        assertThrows(RegraNegocioException.class, () -> produtoService.cadastrar(invalido));
    }

    @Test
    void deveRejeitarPrecoDeVendaNegativo() {
        Produto invalido = produtoValido();
        invalido.setPrecoVenda(new BigDecimal("-1.00"));

        assertThrows(RegraNegocioException.class, () -> produtoService.cadastrar(invalido));
    }

    @Test
    void deveRejeitarQuantidadeNegativa() {
        Produto invalido = produtoValido();
        invalido.setQuantidade(-5);

        assertThrows(RegraNegocioException.class, () -> produtoService.cadastrar(invalido));
    }

    @Test
    void deveCalcularMargemDeLucroCorretamente() {
        Produto p = produtoValido();
        p.setPrecoCusto(new BigDecimal("100.00"));
        p.setPrecoVenda(new BigDecimal("150.00"));

        assertEquals(0, new BigDecimal("50.00").compareTo(p.calcularLucro()));
        assertEquals(0, new BigDecimal("50.00").compareTo(p.calcularMargemLucro()));
    }

    @Test
    void deveAtualizarProdutoExistente() {
        Produto salvo = produtoService.cadastrar(produtoValido());
        salvo.setNomeProduto("Mouse Optico Sem Fio");
        salvo.setQuantidade(20);

        produtoService.atualizar(salvo);

        Produto atualizado = produtoService.buscarPorId(salvo.getIdProduto()).orElseThrow();
        assertEquals("Mouse Optico Sem Fio", atualizado.getNomeProduto());
        assertEquals(20, atualizado.getQuantidade());
    }

    @Test
    void deveRemoverProdutoExistente() {
        Produto salvo = produtoService.cadastrar(produtoValido());

        produtoService.remover(salvo.getIdProduto());

        assertTrue(produtoService.buscarPorId(salvo.getIdProduto()).isEmpty());
    }

    @Test
    void deveLancarErroAoRemoverProdutoInexistente() {
        assertThrows(RegraNegocioException.class, () -> produtoService.remover(9999));
    }
}
