package com.estoqueti.core.config;

import com.estoqueti.core.exception.DAOException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Responsavel exclusivamente por fornecer conexoes JDBC configuradas.
 *
 * Refatoracao em relacao a dao.Conexao (projeto desktop):
 *   1. As credenciais deixam de estar hardcoded no .java e passam a vir de
 *      um arquivo externo (db.properties) - Single Responsibility Principle:
 *      esta classe so sabe "como montar uma conexao", nao guarda estado nem
 *      decide quando abrir/fechar (isso e responsabilidade de quem usa).
 *   2. Cada chamada a getConnection() retorna uma conexao NOVA. As classes de
 *      repositorio abrem e fecham a conexao a cada operacao (try-with-resources),
 *      evitando o problema do projeto original, em que cada DAO abria 1 unica
 *      Connection no construtor e a mantinha para sempre (risco de conexao
 *      caida/expirada em uma aplicacao de longa duracao).
 *   3. Erros de conexao agora sao propagados como DAOException (checked),
 *      em vez de serem apenas impressos no console com System.out.println
 *      e a chamada retornar null silenciosamente.
 */
public final class DatabaseConfig {

    private static final String ARQUIVO_CONFIG = "db.properties";
    private static final Properties propriedades = new Properties();
    private static boolean carregado = false;

    private DatabaseConfig() {
        // classe utilitaria, nao deve ser instanciada
    }

    private static synchronized void carregarPropriedadesSeNecessario() {
        if (carregado) {
            return;
        }
        try (InputStream in = DatabaseConfig.class.getClassLoader()
                .getResourceAsStream(ARQUIVO_CONFIG)) {
            if (in == null) {
                throw new DAOException(
                    "Arquivo de configuracao '" + ARQUIVO_CONFIG + "' nao encontrado no classpath.");
            }
            propriedades.load(in);
            carregado = true;
        } catch (IOException e) {
            throw new DAOException("Falha ao ler " + ARQUIVO_CONFIG, e);
        }
    }

    /**
     * Abre e retorna uma nova conexao JDBC.
     * Quem chamar este metodo e responsavel por fechar a conexao
     * (idealmente via try-with-resources).
     */
    public static Connection getConnection() {
        carregarPropriedadesSeNecessario();
        try {
            String url = propriedades.getProperty("db.url");
            String user = propriedades.getProperty("db.user");
            String senha = propriedades.getProperty("db.password");
            return DriverManager.getConnection(url, user, senha);
        } catch (SQLException e) {
            throw new DAOException("Erro ao conectar ao banco de dados.", e);
        }
    }
}
