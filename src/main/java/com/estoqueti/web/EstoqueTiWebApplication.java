package com.estoqueti.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Ponto de entrada da aplicacao web EstoqueTI (Etapa 9).
 *
 * scanBasePackages="com.estoqueti" faz o component-scan (@Service,
 * @RestController, @Repository) cobrir tambem com.estoqueti.core, onde
 * vivem os models, services e repositorios reaproveitados da Etapa 6.
 *
 * IMPORTANTE: por padrao, o Spring Boot so procura @Entity e interfaces
 * JpaRepository dentro do pacote da propria classe principal (e subpacotes),
 * mesmo quando scanBasePackages e informado - esse e um comportamento
 * separado do component-scan comum. Como os models JPA ficam em
 * com.estoqueti.core.model e os repositorios Spring Data em
 * com.estoqueti.core.repository.jpa (pacotes "irmaos" de com.estoqueti.web,
 * nao subpacotes), e preciso declarar @EntityScan e @EnableJpaRepositories
 * explicitamente apontando para eles - sem isso, a aplicacao sobe sem
 * nenhuma tabela/repositorio mapeado.
 */
@SpringBootApplication(scanBasePackages = "com.estoqueti")
@EntityScan(basePackages = "com.estoqueti.core.model")
@EnableJpaRepositories(basePackages = "com.estoqueti.core.repository.jpa")
public class EstoqueTiWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(EstoqueTiWebApplication.class, args);
    }
}
