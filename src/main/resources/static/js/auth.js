/**
 * auth.js — Etapa 9: o LOGIN agora é validado no servidor (POST
 * /api/auth/login, que usa AutenticacaoService/UsuarioRepository reais
 * contra o banco). Não existe sessão do lado do servidor nesta etapa
 * (escopo academico): após autenticar, o front-end guarda o usuário
 * retornado (sem a senha, que o backend nunca devolve) em localStorage,
 * só para saber "quem está logado" e mostrar/ocultar telas por perfil -
 * exatamente como fazia na Etapa 8, só que agora a validação da senha
 * é real, feita pelo Spring Boot.
 */
const EstoqueAuth = (function () {
  const SESSION_KEY = 'estoqueti_sessao';

  async function login(loginUsuario, senha) {
    // Lança (throw) se as credenciais forem inválidas - o backend responde
    // 400 com {"mensagem": "..."} e EstoqueAPI converte isso em uma
    // exceção JS com essa mensagem (ver js/api.js).
    const usuario = await EstoqueAPI.auth.login(loginUsuario, senha);
    localStorage.setItem(SESSION_KEY, JSON.stringify({
      idUsuario: usuario.idUsuario,
      nomeUsuario: usuario.nomeUsuario,
      nivelAcesso: usuario.nivelAcesso,
      login: usuario.login,
    }));
    return usuario;
  }

  function logout() {
    localStorage.removeItem(SESSION_KEY);
    window.location.href = 'index.html';
  }

  function sessaoAtual() {
    try { return JSON.parse(localStorage.getItem(SESSION_KEY)); } catch { return null; }
  }

  function exigirLogin() {
    if (!sessaoAtual()) window.location.href = 'index.html';
  }

  function iniciais(nome) {
    return nome.split(' ').filter(Boolean).slice(0, 2).map((p) => p[0]).join('').toUpperCase();
  }

  return { login, logout, sessaoAtual, exigirLogin, iniciais };
})();
