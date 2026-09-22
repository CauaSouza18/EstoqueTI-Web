document.addEventListener('DOMContentLoaded', () => {
  if (EstoqueAuth.sessaoAtual()) { window.location.href = 'dashboard.html'; return; }

  const form = document.getElementById('form-login');
  const togglePass = document.getElementById('toggle-pass');
  const inputSenha = document.getElementById('senha');
  const erroGeral = document.getElementById('erro-geral');
  const btnEntrar = document.getElementById('btn-entrar');

  togglePass.addEventListener('click', () => {
    const showing = inputSenha.type === 'text';
    inputSenha.type = showing ? 'password' : 'text';
    togglePass.textContent = showing ? 'mostrar' : 'ocultar';
  });

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    erroGeral.style.display = 'none';

    const regras = {
      login: [EstoqueUtils.validadores.obrigatorio('Informe seu usuário.')],
      senha: [EstoqueUtils.validadores.obrigatorio('Informe sua senha.'), EstoqueUtils.validadores.minLen(6, 'A senha deve ter ao menos 6 caracteres.')],
    };
    if (!EstoqueUtils.validarFormulario(form, regras)) return;

    const loginVal = form.login.value.trim();
    const senhaVal = form.senha.value;

    btnEntrar.disabled = true;
    btnEntrar.textContent = 'Entrando...';
    try {
      const usuario = await EstoqueAuth.login(loginVal, senhaVal);
      EstoqueUtils.toast(`Bem-vindo(a), ${usuario.nomeUsuario.split(' ')[0]}!`);
      setTimeout(() => { window.location.href = 'dashboard.html'; }, 300);
    } catch (erro) {
      // O backend responde 400 com {"mensagem": "Login ou senha invalidos."}
      // para credenciais erradas - ver AutenticacaoService (Etapa 6) e
      // GlobalExceptionHandler (Etapa 9).
      erroGeral.textContent = erro.message || 'Não foi possível entrar. Tente novamente.';
      erroGeral.style.display = 'block';
      btnEntrar.disabled = false;
      btnEntrar.textContent = 'Entrar';
    }
  });

  document.querySelectorAll('[data-demo-login]').forEach((btn) => {
    btn.addEventListener('click', () => {
      form.login.value = btn.dataset.demoLogin;
      form.senha.value = btn.dataset.demoSenha;
    });
  });
});
