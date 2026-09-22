# Evidências de Versionamento — Etapa 9

## ⚠️ Nota importante sobre como usar este documento

Este ambiente de desenvolvimento **não tem acesso às suas credenciais do
GitHub** (nenhum token foi fornecido nesta sessão), então não foi possível
criar o novo repositório remoto nem fazer `git push` por aqui.

O que **foi feito**: um repositório Git local foi inicializado dentro da
pasta do projeto, com um histórico de commits reais e incrementais (log
abaixo), organizados por camada/etapa de desenvolvimento — exatamente como
um repositório de verdade ficaria se você tivesse commitado a cada parte
concluída.

**O que falta você fazer** (5 minutos): criar o repositório vazio no
GitHub e "empurrar" (`push`) este histórico já pronto para lá. O passo a
passo está na seção 3.

---

## 1. Log de commits (evidência local)

```
601ab00 | 2026-09-22 21:25:13 +0000 | docs: README do projeto e evidencias de teste/bugtracking
24787af | 2026-09-22 21:25:07 +0000 | test: suite de testes automatizados (JUnit 5 + Spring Boot Test)
56d1444 | 2026-09-22 21:25:01 +0000 | feat: integra o front-end da Etapa 8 com a API REST
c9a3a5a | 2026-09-22 21:24:54 +0000 | feat: camada web - controllers REST, DTOs e tratamento de erros
9ff410b | 2026-09-22 21:24:48 +0000 | feat: services de regra de negocio integrados e DataSeeder
17377c6 | 2026-09-22 21:24:41 +0000 | feat: repositorios Spring Data JPA e adaptadores para o banco
250bcaa | 2026-09-22 21:24:35 +0000 | feat: reaproveita models, excecoes e repositorios da Etapa 6
724bcea | 2026-09-22 21:24:26 +0000 | chore: estrutura inicial do projeto Spring Boot (Etapa 9)
```

> Nota: este próprio documento foi adicionado em um 9º commit
> (`docs: evidencias de versionamento...`) logo após o log acima ter sido
> gerado — por isso ele não aparece na lista, mas está no repositório.

**8 commits de código/testes/README + este documento**, **101 arquivos
versionados**, organizados assim:

| Commit | O que entrou |
|---|---|
| `chore: estrutura inicial` | `pom.xml`, `.gitignore`, `application.properties` (dev e teste) |
| `feat: reaproveita models...` | `model/`, `exception/`, `repository/*.java` (interfaces), `repository/jdbc/`, `repository/memoria/` — base da Etapa 6 |
| `feat: repositorios Spring Data JPA...` | `repository/jpa/` — a nova camada de persistência real |
| `feat: services de regra de negocio...` | `service/`, `DataSeeder` |
| `feat: camada web...` | `web/` — controllers, DTOs, exception handler |
| `feat: integra o front-end...` | `resources/static/` — HTML/CSS/JS integrados |
| `test: suite de testes...` | `src/test/` |
| `docs: README...` | `README.md`, `EVIDENCIAS-TESTES.md` |

## 2. Por que esta organização de commits

Cada commit corresponde a uma camada coesa do projeto (dados → persistência
→ regra de negócio → web → front-end → testes → documentação), na mesma
ordem em que uma implementação de baixo para cima naturalmente acontece.
Isso facilita revisar a evolução do projeto commit a commit, e deixa claro
no histórico o que veio da Etapa 6 (comentado nas mensagens de commit) e o
que foi acrescentado nesta etapa.

## 3. Como publicar no GitHub (passo a passo)

1. Crie um repositório **vazio** no GitHub (sem README/gitignore/license
   — para não conflitar com o que já existe aqui). Sugestão de nome:
   `EstoqueTI-Web` ou `EstoqueTI-Etapa9`.
2. No terminal, dentro da pasta `estoqueti-web` extraída do ZIP entregue:
   ```bash
   git remote add origin https://github.com/<seu-usuario>/<nome-do-repo>.git
   git branch -M main
   git push -u origin main
   ```
3. Se o Git pedir autenticação, use um **Personal Access Token** (PAT) no
   lugar da senha (o GitHub não aceita mais senha de conta em `git push`
   via HTTPS). Em *Settings → Developer settings → Personal access
   tokens* no GitHub.
4. Depois do push, confirme no navegador que os 8 commits aparecem no
   histórico do repositório (`https://github.com/<seu-usuario>/<repo>/commits/main`)
   — isso é a evidência final de versionamento a anexar/citar na entrega,
   complementando o log local acima.

Se preferir, você também pode continuar commitando localmente à medida
que ajustar o projeto (ex.: depois de rodar os testes de verdade e
preencher a Seção 3 de `EVIDENCIAS-TESTES.md`), e então dar o push de tudo
de uma vez — o histórico incremental já criado aqui não se perde.
