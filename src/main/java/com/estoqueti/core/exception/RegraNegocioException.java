package com.estoqueti.core.exception;

/**
 * Sinaliza violacao de uma regra de negocio (ex.: nome obrigatorio, estoque
 * insuficiente para uma saida, permissao insuficiente do usuario).
 *
 * No projeto desktop essas validacoes ficavam misturadas dentro dos metodos
 * das telas (ex.: TelaProdutos.salvarProduto()) e o feedback ao usuario era
 * feito chamando JOptionPane diretamente de dentro da regra de validacao -
 * ou seja, a regra de negocio "sabia" que existia uma interface Swing.
 * Agora a camada de servico apenas lanca esta excecao com uma mensagem
 * amigavel; quem estiver na ponta (Swing, servlet, controller REST etc.)
 * decide como exibi-la.
 */
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
