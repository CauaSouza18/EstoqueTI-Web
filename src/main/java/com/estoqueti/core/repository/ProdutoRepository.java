package com.estoqueti.core.repository;

import com.estoqueti.core.model.Produto;
import java.util.List;
import java.util.Optional;

/**
 * Contrato de persistencia de Produto.
 *
 * No projeto desktop, ProdutoDAO era uma classe concreta usada diretamente
 * pelas telas, e alem disso estava incompleta (so tinha listar() e salvar()
 * para INSERT; o UPDATE tinha sido implementado por fora, dentro da propria
 * tela, em SQL puro). Aqui o contrato e explicito e completo (CRUD), e as
 * camadas de servico dependem desta interface, nao de uma implementacao
 * concreta - Dependency Inversion Principle. Isso permite trocar a
 * implementacao (JDBC/MySQL hoje, outro banco ou uma API amanha) sem alterar
 * nenhuma linha da camada de regra de negocio.
 */
public interface ProdutoRepository {

    List<Produto> listarTodos();

    Optional<Produto> buscarPorId(int idProduto);

    Produto salvar(Produto produto);

    void atualizar(Produto produto);

    void atualizarQuantidade(int idProduto, int novaQuantidade);

    /** Adicionado na Etapa 9: a tela web de produtos (Etapa 8) permite excluir. */
    void remover(int idProduto);
}
