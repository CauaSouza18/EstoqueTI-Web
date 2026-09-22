/**
 * sidebar.js — comportamento comum a todas as páginas internas:
 * valida sessão, preenche dados do usuário logado, liga o botão
 * de sair e mantém o relógio da topbar atualizado.
 */
document.addEventListener('DOMContentLoaded', () => {
  EstoqueAuth.exigirLogin();
  const sessao = EstoqueAuth.sessaoAtual();
  if (!sessao) return;

  const nomeEl = document.querySelector('[data-user-name]');
  const nivelEl = document.querySelector('[data-user-level]');
  const avatarEl = document.querySelector('[data-user-avatar]');
  if (nomeEl) nomeEl.textContent = sessao.nomeUsuario;
  if (nivelEl) nivelEl.textContent = sessao.nivelAcesso;
  if (avatarEl) avatarEl.textContent = EstoqueAuth.iniciais(sessao.nomeUsuario);

  if (sessao.nivelAcesso !== 'Administrador') {
    document.querySelectorAll('[data-admin-only]').forEach((el) => el.remove());
  }

  const logoutBtn = document.querySelector('[data-action="logout"]');
  if (logoutBtn) logoutBtn.addEventListener('click', () => EstoqueAuth.logout());

  const clockEl = document.querySelector('[data-clock]');
  if (clockEl) {
    const tick = () => {
      clockEl.textContent = new Date().toLocaleString('pt-BR', {
        day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit',
      });
    };
    tick();
    setInterval(tick, 30000);
  }
});
