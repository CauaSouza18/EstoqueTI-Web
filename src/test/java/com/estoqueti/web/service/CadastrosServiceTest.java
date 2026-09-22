package com.estoqueti.web.service;

import com.estoqueti.core.exception.RegraNegocioException;
import com.estoqueti.core.model.Categoria;
import com.estoqueti.core.model.Fornecedor;
import com.estoqueti.core.model.NotaFiscal;
import com.estoqueti.core.service.CategoriaService;
import com.estoqueti.core.service.FornecedorService;
import com.estoqueti.core.service.NotaFiscalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CadastrosServiceTest {

    @Autowired private CategoriaService categoriaService;
    @Autowired private FornecedorService fornecedorService;
    @Autowired private NotaFiscalService notaFiscalService;

    // ---------------- Categoria ----------------

    @Test
    void deveCadastrarERenomearCategoria() {
        Categoria c = categoriaService.cadastrar("Perifericos");
        categoriaService.atualizar(c.getIdCategoria(), "Perifericos e Acessorios");

        Categoria atualizada = categoriaService.listarTodas().stream()
                .filter(x -> x.getIdCategoria() == c.getIdCategoria()).findFirst().orElseThrow();
        assertEquals("Perifericos e Acessorios", atualizada.getNomeCategoria());
    }

    @Test
    void deveRejeitarCategoriaComNomeVazio() {
        assertThrows(RegraNegocioException.class, () -> categoriaService.cadastrar("   "));
    }

    @Test
    void deveRemoverCategoria() {
        Categoria c = categoriaService.cadastrar("Temporaria");
        categoriaService.remover(c.getIdCategoria());
        assertTrue(categoriaService.listarTodas().stream().noneMatch(x -> x.getIdCategoria() == c.getIdCategoria()));
    }

    // ---------------- Fornecedor ----------------

    private Fornecedor fornecedorValido() {
        return new Fornecedor(0, "TechDistrib Ltda", "Av. Industrial, 1200", "(11) 3344-5566",
                "contato@techdistrib.com.br", "12.345.678/0001-90", "Ativo");
    }

    @Test
    void deveCadastrarFornecedorValido() {
        Fornecedor salvo = fornecedorService.cadastrar(fornecedorValido());
        assertTrue(salvo.getIdFornecedor() > 0);
    }

    @Test
    void deveRejeitarFornecedorSemCnpj() {
        Fornecedor invalido = fornecedorValido();
        invalido.setCnpj("");
        assertThrows(RegraNegocioException.class, () -> fornecedorService.cadastrar(invalido));
    }

    @Test
    void deveRemoverFornecedor() {
        Fornecedor salvo = fornecedorService.cadastrar(fornecedorValido());
        fornecedorService.remover(salvo.getIdFornecedor());
        assertThrows(RegraNegocioException.class, () -> fornecedorService.remover(salvo.getIdFornecedor()));
    }

    // ---------------- Nota Fiscal ----------------

    @Test
    void deveCadastrarNotaFiscalComFornecedorValido() {
        Fornecedor f = fornecedorService.cadastrar(fornecedorValido());
        NotaFiscal nf = notaFiscalService.cadastrar(
                new NotaFiscal(0, "000123", LocalDate.now(), new BigDecimal("1200.00"), f.getIdFornecedor()));

        assertTrue(nf.getIdNf() > 0);
    }

    @Test
    void deveRejeitarNotaFiscalComFornecedorInexistente() {
        NotaFiscal invalida = new NotaFiscal(0, "000999", LocalDate.now(), new BigDecimal("100.00"), 9999);
        assertThrows(RegraNegocioException.class, () -> notaFiscalService.cadastrar(invalida));
    }

    @Test
    void deveRejeitarNotaFiscalComValorZeroOuNegativo() {
        Fornecedor f = fornecedorService.cadastrar(fornecedorValido());
        NotaFiscal invalida = new NotaFiscal(0, "000124", LocalDate.now(), BigDecimal.ZERO, f.getIdFornecedor());
        assertThrows(RegraNegocioException.class, () -> notaFiscalService.cadastrar(invalida));
    }

    @Test
    void deveAtualizarERemoverNotaFiscal() {
        Fornecedor f = fornecedorService.cadastrar(fornecedorValido());
        NotaFiscal nf = notaFiscalService.cadastrar(
                new NotaFiscal(0, "000125", LocalDate.now(), new BigDecimal("500.00"), f.getIdFornecedor()));

        nf.setValorTotal(new BigDecimal("777.00"));
        notaFiscalService.atualizar(nf);
        assertEquals(0, new BigDecimal("777.00").compareTo(notaFiscalService.buscarPorId(nf.getIdNf()).orElseThrow().getValorTotal()));

        notaFiscalService.remover(nf.getIdNf());
        assertTrue(notaFiscalService.buscarPorId(nf.getIdNf()).isEmpty());
    }
}
