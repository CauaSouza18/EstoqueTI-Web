package com.estoqueti.web;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Teste de "smoke test": garante que todo o contexto Spring sobe sem
 * erros - todas as @Entity mapeiam corretamente, todos os @Service e
 * @RestController conseguem ser instanciados e injetados (ProdutoService
 * recebe ProdutoRepository, que recebe ProdutoJpaRepository, etc).
 * Se qualquer fio da injecao de dependencia estiver quebrado, este teste
 * falha imediatamente - antes mesmo de testar regras de negocio.
 */
@SpringBootTest
@ActiveProfiles("test")
class EstoqueTiWebApplicationTests {

    @Test
    void contextLoads() {
        // Intencionalmente vazio: o teste passa se o contexto Spring
        // inicializar sem lançar exceção.
    }
}
