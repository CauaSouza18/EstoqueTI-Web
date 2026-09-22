package com.estoqueti.core.service;

import com.estoqueti.core.exception.RegraNegocioException;
import com.estoqueti.core.model.MovimentacaoEstoque;
import com.estoqueti.core.model.Produto;
import com.estoqueti.core.model.Usuario;
import com.estoqueti.core.repository.MovimentacaoEstoqueRepository;
import com.estoqueti.core.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Regras de negocio de Movimentacao de Estoque.
 *
 * No projeto desktop, dao.MovimentacaoEstoqueDAO fazia tres coisas ao mesmo
 * tempo: validar se havia estoque suficiente (temEstoqueSuficiente),
 * gerenciar a transacao SQL (commit/rollback) e persistir os dados - uma
 * unica classe de acesso a dados concentrando regra de negocio E
 * persistencia. Nesta refatoracao:
 *   - a validacao "tem estoque suficiente?" e uma decisao de negocio e
 *     mora aqui;
 *   - a permissao "este usuario pode registrar uma movimentacao?" tambem e
 *     validada aqui, reaproveitando Usuario.podeOperar() (que ja existia no
 *     modelo e nao precisou ser reescrito);
 *   - o repositorio (MovimentacaoEstoqueRepository / ProdutoRepository) so
 *     sabe gravar e ler dados.
 * O uso conjunto de dois repositorios dentro de um mesmo metodo de servico
 * e o motivo pelo qual essa camada existe: e aqui, e nao no DAO, que faz
 * sentido coordenar "gravar a movimentacao" + "atualizar o saldo do
 * produto" como uma unica operacao de negocio.
 */
@Service
public class MovimentacaoEstoqueService {

    private final MovimentacaoEstoqueRepository movimentacaoRepository;
    private final ProdutoRepository produtoRepository;

    public MovimentacaoEstoqueService(MovimentacaoEstoqueRepository movimentacaoRepository,
                                       ProdutoRepository produtoRepository) {
        this.movimentacaoRepository = movimentacaoRepository;
        this.produtoRepository = produtoRepository;
    }

    public List<MovimentacaoEstoque> listarTodas() {
        return movimentacaoRepository.listarTodas();
    }

    public List<MovimentacaoEstoque> listarUltimas(int limite) {
        return movimentacaoRepository.listarUltimas(limite);
    }

    public int contarMovimentacoesHoje() {
        return movimentacaoRepository.contarHoje();
    }

    /**
     * Registra uma entrada ou saida de estoque, validando permissao do
     * usuario e, em caso de saida, se ha quantidade suficiente.
     */
    public MovimentacaoEstoque registrar(Usuario usuarioLogado, int idProduto, String tipoMovimentacao,
                                          int quantidade, String observacao) {
        if (!usuarioLogado.podeOperar()) {
            throw new RegraNegocioException(
                "Usuario '" + usuarioLogado.getLogin() + "' nao tem permissao para movimentar estoque.");
        }
        if (quantidade <= 0) {
            throw new RegraNegocioException("Quantidade da movimentacao deve ser maior que zero.");
        }

        Produto produto = produtoRepository.buscarPorId(idProduto)
                .orElseThrow(() -> new RegraNegocioException("Produto id=" + idProduto + " nao encontrado."));

        boolean entrada = "Entrada".equals(tipoMovimentacao);
        if (!entrada && produto.getQuantidade() < quantidade) {
            throw new RegraNegocioException(
                "Estoque insuficiente para saida: disponivel=" + produto.getQuantidade()
                + ", solicitado=" + quantidade);
        }

        MovimentacaoEstoque mov = new MovimentacaoEstoque();
        mov.setIdProduto(idProduto);
        mov.setTipoMovimentacao(tipoMovimentacao);
        mov.setQuantidade(quantidade);
        mov.setObservacao(observacao);
        mov.setIdUsuario(usuarioLogado.getIdUsuario());

        movimentacaoRepository.registrar(mov);

        int novaQuantidade = entrada
                ? produto.getQuantidade() + quantidade
                : produto.getQuantidade() - quantidade;
        produtoRepository.atualizarQuantidade(idProduto, novaQuantidade);
        produto.setQuantidade(novaQuantidade);

        return mov;
    }
}
