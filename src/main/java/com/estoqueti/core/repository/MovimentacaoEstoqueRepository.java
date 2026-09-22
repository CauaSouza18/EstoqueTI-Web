package com.estoqueti.core.repository;

import com.estoqueti.core.model.MovimentacaoEstoque;
import java.util.List;

/**
 * Contrato de persistencia de MovimentacaoEstoque.
 *
 * Repare que NAO existe aqui um metodo "temEstoqueSuficiente(...)": no
 * projeto desktop, dao.MovimentacaoEstoqueDAO tinha esse metodo, o que
 * misturava regra de negocio (validar se pode dar saida) com persistencia
 * (SELECT no banco). Nesta refatoracao, o repositorio so busca e grava dados;
 * a validacao de estoque suficiente foi movida para
 * service.MovimentacaoEstoqueService, que e quem decide.
 */
public interface MovimentacaoEstoqueRepository {

    List<MovimentacaoEstoque> listarTodas();

    List<MovimentacaoEstoque> listarPorTipo(String tipo);

    List<MovimentacaoEstoque> listarPorProduto(int idProduto);

    List<MovimentacaoEstoque> listarUltimas(int limite);

    int contarHoje();

    /** Insere a movimentacao. Nao mexe na quantidade do produto (isso e feito a parte, na service, dentro da mesma transacao). */
    void registrar(MovimentacaoEstoque movimentacao);
}
