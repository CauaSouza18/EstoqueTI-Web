package com.estoqueti.core.exception;

/**
 * Sinaliza falhas na camada de persistencia (SQL, conexao, mapeamento).
 *
 * Substitui o padrao usado no projeto desktop, em que toda excecao de banco
 * era apenas impressa com System.out.println e o metodo retornava
 * null/false/lista-vazia. Isso escondia falhas reais do restante do sistema
 * (ex.: a tela achava que "cadastrar" tinha falhado por regra de negocio,
 * quando na verdade o banco estava fora do ar). Ao propagar uma excecao,
 * a camada de servico e a camada de apresentacao decidem como reagir
 * (mostrar mensagem, logar, tentar novamente etc.), em vez de a propria
 * classe de acesso a dados tomar essa decisao por elas.
 */
public class DAOException extends RuntimeException {

    public DAOException(String mensagem) {
        super(mensagem);
    }

    public DAOException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
