/**
 * api.js
 * ---------------------------------------------------------
 * Cliente da API REST do backend Spring Boot (Etapa 9).
 *
 * Ate a Etapa 8, EstoqueDB simulava os repositorios da Etapa 6 usando
 * localStorage, com metodos sincronos (listar(), salvar()...). Agora que
 * existe um back-end real, EstoqueAPI expõe a MESMA forma de uso (listar,
 * buscarPorId, salvar, atualizar, remover por recurso) so que via fetch()
 * e retornando Promises - por isso as paginas passam a usar
 * async/await, mas a logica de cada tela muda o minimo possivel.
 *
 * Todas as rotas sao relativas (mesma origem: o proprio Spring Boot serve
 * este front-end como recurso estatico), entao nao ha problema de CORS.
 * ---------------------------------------------------------
 */
const EstoqueAPI = (function () {

  async function requisitar(caminho, opcoes = {}) {
    const resposta = await fetch(caminho, {
      headers: { 'Content-Type': 'application/json' },
      ...opcoes,
    });

    if (resposta.status === 204) return null;

    let corpo = null;
    const texto = await resposta.text();
    if (texto) {
      try { corpo = JSON.parse(texto); } catch { corpo = texto; }
    }

    if (!resposta.ok) {
      const mensagem = (corpo && corpo.mensagem) ? corpo.mensagem : `Erro ${resposta.status} ao acessar ${caminho}.`;
      throw new Error(mensagem);
    }
    return corpo;
  }

  function get(caminho) { return requisitar(caminho); }
  function post(caminho, dados) { return requisitar(caminho, { method: 'POST', body: JSON.stringify(dados) }); }
  function put(caminho, dados) { return requisitar(caminho, { method: 'PUT', body: JSON.stringify(dados) }); }
  function del(caminho) { return requisitar(caminho, { method: 'DELETE' }); }

  function recurso(base, idField) {
    return {
      listar: () => get(base),
      buscarPorId: (id) => get(`${base}/${id}`),
      salvar: (dados) => post(base, dados),
      atualizar: (id, dados) => put(`${base}/${id}`, dados),
      remover: (id) => del(`${base}/${id}`),
    };
  }

  return {
    categorias: recurso('/api/categorias', 'idCategoria'),
    produtos: recurso('/api/produtos', 'idProduto'),
    fornecedores: recurso('/api/fornecedores', 'idFornecedor'),
    notasFiscais: recurso('/api/notas-fiscais', 'idNf'),
    usuarios: recurso('/api/usuarios', 'idUsuario'),

    movimentacoes: {
      listar: () => get('/api/movimentacoes'),
      listarRecentes: (limite) => get(`/api/movimentacoes/recentes?limite=${limite}`),
      registrar: (dados) => post('/api/movimentacoes', dados),
    },

    auth: {
      login: (login, senha) => post('/api/auth/login', { login, senha }),
    },
  };
})();
