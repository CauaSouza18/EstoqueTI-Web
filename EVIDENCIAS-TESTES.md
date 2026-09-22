# Evidências de Teste e Bugtracking — Etapa 9

## ⚠️ Nota importante sobre como usar este documento

O ambiente onde este projeto foi escrito **não tem acesso ao repositório
Maven Central**, então não foi possível executar `mvn spring-boot:run` nem
`mvn test` para gerar a saída real de execução (que normalmente seria o
melhor tipo de evidência a anexar aqui: prints ou logs do console do
NetBeans/Maven).

Por isso, este documento tem duas partes:

1. **Plano de teste** — o que cada teste automatizado cobre e por quê
   (isso você pode conferir lendo o código em `src/test/java`).
2. **Bugtracking real** — problemas efetivamente encontrados e corrigidos
   durante o desenvolvimento desta etapa, via revisão cruzada
   automatizada (scripts comparando HTML ↔ JS ↔ Controllers) e revisão
   manual linha a linha, já que não havia como "rodar e ver quebrar".

**Antes de entregar**, rode `mvn test` no NetBeans (ou `mvn test` no
terminal, dentro da pasta do projeto) e substitua a seção 3 abaixo pelo
print real do resultado (verde/vermelho) — é rápido, o suite tem 30+
casos e deve terminar em poucos segundos com o H2 em memória.

---

## 1. Plano de teste — o que está coberto

Os testes ficam em `src/test/java/com/estoqueti/web/`. Todos rodam com
`@SpringBootTest` + perfil `test` (H2 em memória, recriado do zero a cada
execução) e `@Transactional` (rollback automático ao final de cada teste,
garantindo isolamento).

| Classe de teste | Regra de negócio coberta | Origem |
|---|---|---|
| `EstoqueTiWebApplicationTests` | O contexto Spring sobe sem erros (todas as entidades mapeiam, todos os beans injetam) | Novo (Etapa 9) |
| `ProdutoServiceTest` | Cadastro válido; rejeição de nome vazio, categoria inexistente, preço negativo, quantidade negativa; cálculo de margem de lucro; atualizar; remover; remover inexistente | Etapa 7 (ProdutoServiceTest original) + casos novos de remover/atualizar |
| `MovimentacaoEstoqueServiceTest` | Entrada aumenta estoque; saída diminui estoque; saída com estoque insuficiente é rejeitada **e não altera o estoque**; usuário sem permissão (`Consulta`) não pode movimentar; quantidade ≤ 0 é rejeitada; produto inexistente é rejeitado; movimentação aparece na listagem | Etapa 7 (MovimentacaoEstoqueServiceTest original), expandido |
| `AutenticacaoServiceTest` | Login e senha corretos autenticam; login é case-insensitive; senha errada rejeita; login inexistente rejeita | Etapa 7 (AutenticacaoServiceTest original) |
| `UsuarioServiceTest` | Cadastro válido; senha curta rejeitada; login duplicado rejeitado (cadastro e edição); edição com senha em branco mantém a senha atual; remover | Novo (Etapa 9 — `UsuarioService` não existia na Etapa 6/7) |
| `CadastrosServiceTest` | Categoria: cadastrar/renomear/remover, nome vazio rejeitado. Fornecedor: cadastro válido, CNPJ obrigatório, remover. Nota Fiscal: cadastro com fornecedor válido, fornecedor inexistente rejeitado, valor ≤ 0 rejeitado, atualizar e remover | Novo (Etapa 9 — cobre os métodos acrescentados às interfaces) |

**Total: 7 classes de teste, ~30 casos**, cobrindo tanto as regras de
negócio herdadas da Etapa 6/7 quanto as extensões feitas nesta etapa.

### Por que os testes rodam contra JPA/H2 real (e não contra os mocks em memória da Etapa 7)

Os testes da Etapa 7 validavam a lógica de negócio isoladamente, usando
`ProdutoRepositoryMemoria` no lugar do banco. Nesta etapa, o objetivo
também é validar a **integração** (Service → Repository → JPA →
banco), que é o entregável desta fase — por isso os testes usam
`@SpringBootTest` com o banco H2 real, exercitando a stack completa
adicionada agora: entidades JPA, repositórios Spring Data e os
adaptadores em `repository/jpa/`.

---

## 2. Bugtracking

Formato inspirado em um board de issues (tipo GitHub Issues/Trello),
registrando os problemas encontrados durante o desenvolvimento desta
etapa e como foram corrigidos.

### BUG-01 — Entidades e repositórios JPA fora do escopo de auto-scan
- **Severidade:** Alta (aplicação não subiria)
- **Como foi encontrado:** Revisão manual da configuração do Spring Boot.
- **Descrição:** Por padrão, o Spring Boot só procura `@Entity` e
  interfaces `JpaRepository` dentro do pacote da própria classe principal
  (e subpacotes) — mesmo quando `scanBasePackages` é informado para o
  component-scan comum. Como a classe principal ficou em
  `com.estoqueti.web` e as entidades/repositórios JPA ficam em
  `com.estoqueti.core.*` (pacotes "irmãos", não subpacotes), a aplicação
  subiria sem nenhuma tabela ou repositório mapeado.
- **Correção:** Adicionados `@EntityScan(basePackages = "com.estoqueti.core.model")`
  e `@EnableJpaRepositories(basePackages = "com.estoqueti.core.repository.jpa")`
  explicitamente em `EstoqueTiWebApplication`.
- **Status:** ✅ Corrigido.

### BUG-02 — Interfaces de repositório da Etapa 6 incompletas para o front-end da Etapa 8
- **Severidade:** Alta (funcionalidades da UI ficariam sem back-end)
- **Como foi encontrado:** Comparação manual entre as telas HTML/JS da
  Etapa 8 (que já tinham botões de editar/excluir) e os métodos
  disponíveis nas interfaces `CategoriaRepository`, `FornecedorRepository`,
  `ProdutoRepository`, `NotaFiscalRepository` e `UsuarioRepository` da
  Etapa 6.
- **Descrição:** A Etapa 6 não previa `remover()` para Produto, Categoria,
  Fornecedor e Usuário, nem `atualizar()`/`buscarPorId()` para Categoria,
  Nota Fiscal e Usuário. Sem esses métodos, os botões de editar/excluir da
  Etapa 8 não teriam endpoint correspondente.
- **Correção:** Métodos acrescentados às 5 interfaces afetadas, com
  implementação em `memoria/`, `jdbc/` (por continuidade) e `jpa/`
  (efetivamente usada). Ver comentários `"Adicionado na Etapa 9"` no
  código-fonte de cada interface.
- **Status:** ✅ Corrigido.

### BUG-03 — Senha de usuário exposta em texto puro no JSON da API
- **Severidade:** Média (falha de segurança, não de funcionalidade)
- **Como foi encontrado:** Revisão manual do modelo `Usuario` ao desenhar
  o endpoint `GET /api/usuarios` (usado pela tela administrativa).
- **Descrição:** Serializar a entidade `Usuario` diretamente devolveria o
  campo `senha` em texto puro para qualquer chamada `GET`, inclusive na
  listagem de usuários.
- **Correção:** Anotado `@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)`
  no campo `senha` — a API continua aceitando a senha na entrada
  (cadastro/edição/login), mas nunca a devolve em nenhuma resposta JSON.
  Ajuste consequente em `UsuarioService.atualizar()`: se a senha vier em
  branco na edição (caso do formulário web, que não pré-preenche a senha),
  a senha atual é mantida em vez de apagada.
- **Status:** ✅ Corrigido.

### BUG-04 — `id="crumb-titulo"` ausente em `produto-form.html`
- **Severidade:** Baixa (JS silenciosamente não atualizava o texto da
  página ao editar um produto, sem quebrar o restante da tela)
- **Como foi encontrado:** Script de verificação automatizada
  (comparação de todo `getElementById`/`querySelector` usado em cada
  arquivo `.js` contra os `id` presentes no `.html` correspondente).
- **Descrição:** `js/produto-form.js` referencia
  `document.getElementById('crumb-titulo')` para trocar o texto da
  navegação (breadcrumb) de "Novo produto" para o nome do produto ao
  editar, mas o elemento `<strong>` da breadcrumb em `produto-form.html`
  não tinha esse `id`.
- **Correção:** `id="crumb-titulo"` adicionado ao elemento correspondente.
- **Status:** ✅ Corrigido.

### BUG-05 — `DataSeeder` duplicaria dados de demonstração durante os testes
- **Severidade:** Média (testes ficariam instáveis/poluídos)
- **Como foi encontrado:** Revisão manual ao decidir a estratégia de dados
  de teste vs. dados de demonstração.
- **Descrição:** `DataSeeder` é um `CommandLineRunner` que roda toda vez
  que o contexto Spring sobe — incluindo durante `@SpringBootTest`. Sem
  cuidado, os dados de demonstração (3 usuários, 5 categorias, 7 produtos
  etc.) seriam inseridos em todo teste, conflitando com os dados que cada
  teste cria para si mesmo (ex.: usuário de login duplicado).
- **Correção:** `DataSeeder` anotado com `@Profile("!test")`; o perfil de
  teste usa H2 em memória com `ddl-auto=create-drop` e nenhum seed,
  garantindo que cada classe de teste comece de um banco limpo.
- **Status:** ✅ Corrigido.

### BUG-06 (conhecido, não corrigido nesta etapa) — Exclusão de categoria não valida produtos vinculados no banco
- **Severidade:** Baixa (comportamento herdado do esquema original)
- **Descrição:** Como `Produto.idCategoria` é uma coluna simples (sem
  `@ManyToOne`/chave estrangeira real), excluir uma categoria com produtos
  vinculados não é bloqueado pelo Hibernate/H2 — o produto fica com um
  `idCategoria` "órfão" (a tela de produtos simplesmente exibe "—" no
  lugar do nome da categoria). O front-end já avisa o usuário antes de
  confirmar a exclusão nesse caso ("Esta categoria possui N produto(s)
  vinculado(s). Excluir mesmo assim?").
- **Status:** 🟡 Registrado como limitação conhecida (ver README.md,
  seção "Limitações conhecidas"), correção proposta para uma etapa futura
  (adicionar `@ManyToOne` com `@OnDelete` ou validação explícita no
  `CategoriaService.remover()`).

---

## 3. Espaço para a evidência real de execução (a preencher pelo aluno)

Cole aqui o print ou a saída de texto de `mvn test` rodado no NetBeans,
por exemplo:

```
[INFO] Tests run: XX, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

Se algum teste falhar na sua máquina, é esperado que o bugtracking acima
ganhe uma nova entrada (BUG-07, ...) descrevendo o problema encontrado e
a correção aplicada, fechando o ciclo de bugtracking pedido pela
atividade.
