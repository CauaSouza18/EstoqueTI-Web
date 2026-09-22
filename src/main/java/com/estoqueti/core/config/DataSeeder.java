package com.estoqueti.core.config;

import com.estoqueti.core.model.*;
import com.estoqueti.core.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Popula o banco com dados de demonstracao no primeiro start (Etapa 9).
 *
 * So insere se as tabelas estiverem vazias, para nao duplicar registros a
 * cada reinicializacao da aplicacao (o H2 usado aqui e file-based, entao os
 * dados persistem entre execucoes - ver application.properties).
 *
 * @Profile("!test") impede que este seed rode durante a execucao dos
 * testes automatizados (Etapa 9), que usam o perfil "test" (H2 em memoria,
 * create-drop) e organizam seus proprios dados em cada teste.
 */
@Component
@Profile("!test")
public class DataSeeder implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;
    private final FornecedorRepository fornecedorRepository;
    private final ProdutoRepository produtoRepository;
    private final MovimentacaoEstoqueRepository movimentacaoRepository;
    private final NotaFiscalRepository notaFiscalRepository;
    private final UsuarioRepository usuarioRepository;

    public DataSeeder(CategoriaRepository categoriaRepository, FornecedorRepository fornecedorRepository,
                       ProdutoRepository produtoRepository, MovimentacaoEstoqueRepository movimentacaoRepository,
                       NotaFiscalRepository notaFiscalRepository, UsuarioRepository usuarioRepository) {
        this.categoriaRepository = categoriaRepository;
        this.fornecedorRepository = fornecedorRepository;
        this.produtoRepository = produtoRepository;
        this.movimentacaoRepository = movimentacaoRepository;
        this.notaFiscalRepository = notaFiscalRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void run(String... args) {
        if (!usuarioRepository.listarTodos().isEmpty()) {
            return; // ja tem dados - nao semear de novo
        }

        usuarioRepository.salvar(new Usuario(0, "Caua Souza", "cana.souza", "123456", "Administrador"));
        usuarioRepository.salvar(new Usuario(0, "Bruna Lima", "bruna.lima", "123456", "Operador"));
        usuarioRepository.salvar(new Usuario(0, "Marcos Alves", "marcos.alves", "123456", "Consulta"));

        Categoria notebooks = categoriaRepository.salvar(new Categoria(0, "Notebooks"));
        Categoria perifericos = categoriaRepository.salvar(new Categoria(0, "Perifericos"));
        Categoria redes = categoriaRepository.salvar(new Categoria(0, "Redes"));
        Categoria armazenamento = categoriaRepository.salvar(new Categoria(0, "Armazenamento"));
        Categoria cabos = categoriaRepository.salvar(new Categoria(0, "Cabos e Conectores"));

        Fornecedor techdistrib = fornecedorRepository.salvar(new Fornecedor(0, "TechDistrib Ltda",
                "Av. Industrial, 1200 - Sao Paulo/SP", "(11) 3344-5566", "contato@techdistrib.com.br",
                "12.345.678/0001-90", "Ativo"));
        Fornecedor infoparts = fornecedorRepository.salvar(new Fornecedor(0, "InfoParts Componentes",
                "Rua das Placas, 55 - Campinas/SP", "(19) 3222-1010", "vendas@infoparts.com.br",
                "98.765.432/0001-11", "Ativo"));
        fornecedorRepository.salvar(new Fornecedor(0, "Rede Norte Suprimentos",
                "Al. dos Cabos, 300 - Curitiba/PR", "(41) 3555-7788", "financeiro@redenorte.com.br",
                "45.111.222/0001-33", "Inativo"));

        Produto notebook = produtoRepository.salvar(new Produto(0, "Notebook Dell Vostro 15",
                "Intel i5, 8GB RAM, SSD 256GB", "UN", notebooks.getIdCategoria(), "Dell",
                new BigDecimal("2450.00"), new BigDecimal("3299.00"), 12, "Ativo"));
        Produto mouse = produtoRepository.salvar(new Produto(0, "Mouse Optico USB",
                "Mouse com fio 1000dpi", "UN", perifericos.getIdCategoria(), "Logitech",
                new BigDecimal("18.50"), new BigDecimal("39.90"), 4, "Ativo"));
        Produto switch24 = produtoRepository.salvar(new Produto(0, "Switch 24 Portas Gigabit",
                "Switch gerenciavel 24p", "UN", redes.getIdCategoria(), "TP-Link",
                new BigDecimal("610.00"), new BigDecimal("899.00"), 6, "Ativo"));
        Produto ssd = produtoRepository.salvar(new Produto(0, "SSD NVMe 1TB",
                "Unidade de estado solido M.2", "UN", armazenamento.getIdCategoria(), "Kingston",
                new BigDecimal("320.00"), new BigDecimal("459.00"), 2, "Ativo"));
        Produto cabo = produtoRepository.salvar(new Produto(0, "Cabo de Rede Cat6 (caixa 305m)",
                "Cabo UTP categoria 6", "CX", cabos.getIdCategoria(), "Furukawa",
                new BigDecimal("480.00"), new BigDecimal("690.00"), 9, "Ativo"));
        produtoRepository.salvar(new Produto(0, "Teclado ABNT2 USB",
                "Teclado padrao brasileiro", "UN", perifericos.getIdCategoria(), "Multilaser",
                new BigDecimal("22.00"), new BigDecimal("49.90"), 0, "Inativo"));
        produtoRepository.salvar(new Produto(0, "HD Externo 2TB",
                "Disco rigido portatil USB 3.0", "UN", armazenamento.getIdCategoria(), "Seagate",
                new BigDecimal("340.00"), new BigDecimal("499.00"), 3, "Ativo"));

        notaFiscalRepository.salvar(new NotaFiscal(0, "000123", LocalDate.now().minusDays(1),
                new BigDecimal("12250.00"), techdistrib.getIdFornecedor()));
        notaFiscalRepository.salvar(new NotaFiscal(0, "000098", LocalDate.now().minusDays(3),
                new BigDecimal("8340.00"), infoparts.getIdFornecedor()));
        notaFiscalRepository.salvar(new NotaFiscal(0, "000076", LocalDate.now().minusDays(20),
                new BigDecimal("3120.00"), techdistrib.getIdFornecedor()));

        movimentacaoRepository.registrar(new MovimentacaoEstoque(0, notebook.getIdProduto(),
                LocalDateTime.now().minusDays(1), "Entrada", 5, "Recebimento NF 000123", 1));
        movimentacaoRepository.registrar(new MovimentacaoEstoque(0, mouse.getIdProduto(),
                LocalDateTime.now().minusDays(1), "Saida", 8, "Requisicao setor Suporte", 2));
        movimentacaoRepository.registrar(new MovimentacaoEstoque(0, ssd.getIdProduto(),
                LocalDateTime.now().minusDays(2), "Saida", 3, "Upgrade estacoes TI", 2));
        movimentacaoRepository.registrar(new MovimentacaoEstoque(0, cabo.getIdProduto(),
                LocalDateTime.now().minusDays(3), "Entrada", 10, "Recebimento NF 000098", 1));
        movimentacaoRepository.registrar(new MovimentacaoEstoque(0, switch24.getIdProduto(),
                LocalDateTime.now().minusDays(4), "Entrada", 4, "Recebimento NF 000098", 1));
    }
}
