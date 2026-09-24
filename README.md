# EstoqueTI Web — Etapa 9

Projeto Integrador — conclusão da implementação web do EstoqueTI: back-end
**Spring Boot + Spring Data JPA**, integrando a arquitetura em camadas da
**Etapa 6** e o front-end estático da **Etapa 8**, com persistência real em
banco de dados.

## Como rodar

Requisitos: JDK 17+ e Maven.

```bash
mvn spring-boot:run
```

Depois abra **http://localhost:8080** no navegador. Não é necessário
instalar nenhum banco de dados: o projeto usa H2 em arquivo por padrão
(`./data/estoqueti.mv.db`), criado automaticamente na primeira execução.

Console web do H2 (opcional, para inspecionar as tabelas):
**http://localhost:8080/h2-console** — JDBC URL: `jdbc:h2:file:./data/estoqueti`, usuário `sa`, senha em branco.

**Login de demonstração** (populados por `DataSeeder` na primeira execução):

| Perfil | Login | Senha |
|---|---|---|
| Administrador | `cana.souza` | `123456` |
| Operador | `bruna.lima` | `123456` |
| Consulta | `marcos.alves` | `123456` |

Para usar MySQL em vez de H2 (mesmo banco do projeto desktop original),
veja as instruções comentadas em `src/main/resources/application.properties`.

## Rodando os testes

```bash
mvn test
```

Os testes usam um banco H2 **em memória**, isolado do banco de
desenvolvimento (perfil `test`, ver `src/test/resources/application.properties`),
e cada teste roda dentro de uma transação com rollback automático.

## Arquitetura — como as etapas se encaixam

```
src/main/java/com/estoqueti/
├── core/                        ← Etapa 6 (arquitetura em camadas), com
│   │                               os ajustes desta etapa comentados no
│   │                               código onde aparecem
│   ├── model/                   Produto, Categoria, Fornecedor,
│   │                             MovimentacaoEstoque, NotaFiscal, Usuario
│   │                             — mesma lógica de negócio da Etapa 6,
│   │                             agora também anotados como entidades JPA
│   ├── repository/               Interfaces de repositório da Etapa 6
│   │   ├── jdbc/                 Implementação JDBC/MySQL original —
│   │   │                          mantida por continuidade, não usada em
│   │   │                          runtime nesta etapa (ver nota abaixo)
│   │   ├── memoria/               Implementação em memória — usada nos
│   │   │                          testes unitários originais da Etapa 7
│   │   └── jpa/                   NOVO na Etapa 9: repositórios Spring
│   │                              Data JPA + adaptadores que implementam
│   │                              as MESMAS interfaces de repositório da
│   │                              Etapa 6 (Dependency Inversion Principle
│   │                              em ação — a troca de JDBC por JPA não
│   │                              exigiu alterar uma linha da camada de
│   │                              serviço)
│   ├── service/                  Regras de negócio — copiadas da Etapa 6
│   │                              sem alteração na lógica original, com
│   │                              @Service e os métodos novos que a
│   │                              tela web (Etapa 8) passou a exigir
│   │                              (editar/excluir), comentados no código
│   ├── exception/                RegraNegocioException, DAOException —
│   │                              Etapa 6, inalteradas
│   └── config/                   DatabaseConfig (Etapa 6, JDBC manual,
│                                  mantida por referência) + DataSeeder
│                                  (novo, popula dados de demonstração)
│
└── web/                          NOVO na Etapa 9 — camada web Spring
    ├── EstoqueTiWebApplication   Classe principal Spring Boot
    ├── controller/                Controllers REST (um por recurso),
    │                              finos: só traduzem HTTP ↔ Service
    ├── dto/                       Corpos de requisição/resposta simples
    └── exception/                 Handler global que converte
                                   RegraNegocioException em respostas
                                   JSON 400 padronizadas para o front-end

src/main/resources/
├── application.properties        Configuração do banco (H2 por padrão)
└── static/                       Front-end da Etapa 8, com a camada JS
                                   trocada de localStorage (EstoqueDB) para
                                   chamadas fetch() reais (EstoqueAPI) —
                                   ver js/api.js. HTML e CSS praticamente
                                   inalterados.
```

### Por que os pacotes `jdbc/` e `memoria/` continuam no projeto?

Eles não são usados em tempo de execução nesta etapa (o Spring só
"enxerga" `repository/jpa/*Impl`, que são as únicas classes anotadas
`@Repository`). Foram mantidos para:
1. Evidenciar a evolução do projeto entre as etapas (Etapa 6 → 9);
2. Documentar, na prática, o **Dependency Inversion Principle**: trocar a
   implementação de persistência não exigiu alterar nada na camada de
   serviço, porque ela sempre dependeu apenas das interfaces.

### Ajustes de escopo desta etapa

A Etapa 6 não previa todas as operações que o front-end da Etapa 8 já
implementava (editar categoria, excluir produto/fornecedor/usuário, editar
nota fiscal, CRUD completo de usuários). Esses métodos foram acrescentados
às interfaces de repositório, aos serviços (incluindo um `UsuarioService`
novo) e implementados em todas as camadas — cada acréscimo está comentado
no código com "Adicionado na Etapa 9" para deixar claro o que é herança
da Etapa 6 e o que é extensão desta etapa.

## Limitações conhecidas (escopo acadêmico)

- **Sem sessão no servidor / Spring Security**: o login é validado no
  back-end (senha real, contra o banco), mas não há token nem sessão HTTP.
  O front-end guarda o usuário autenticado em `localStorage` do navegador
  para decidir o que mostrar (ex.: menu "Usuários" só para Administrador).
  Isso é adequado ao escopo do PI, mas não deveria ir para produção sem
  Spring Security.
- **Sem FK real entre Produto e Categoria**: `idCategoria` é uma coluna
  simples (não `@ManyToOne`), replicando o esquema original. Excluir uma
  categoria com produtos vinculados não é bloqueado pelo banco.
- **Senha em texto puro**: mantido assim para não introduzir hashing
  (BCrypt/Spring Security) fora do escopo já grande desta etapa; a senha
  nunca é devolvida pela API (`@JsonProperty(WRITE_ONLY)` em `Usuario`).


