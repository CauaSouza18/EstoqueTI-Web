document.addEventListener('DOMContentLoaded', async () => {
  const sessao = EstoqueAuth.sessaoAtual();
  if (sessao && sessao.nivelAcesso !== 'Administrador') {
    window.location.href = 'dashboard.html';
    return;
  }

  const tbody = document.getElementById('tbody-usuarios');
  const estadoVazio = document.getElementById('estado-vazio');
  const inputBusca = document.getElementById('busca');
  const selectNivel = document.getElementById('filtro-nivel');
  const modalForm = document.getElementById('modal-form');
  const modalExcluir = document.getElementById('modal-excluir');
  const form = document.getElementById('form-usuario');
  let idParaExcluir = null;
  let usuariosCache = [];

  const tagClasse = { Administrador: 'tag--admin', Operador: 'tag--operador', Consulta: 'tag--consulta' };

  async function carregar() {
    usuariosCache = await EstoqueAPI.usuarios.listar();
  }

  function render() {
    let lista = [...usuariosCache];
    const termo = inputBusca.value.trim().toLowerCase();
    if (termo) lista = lista.filter((u) => u.nomeUsuario.toLowerCase().includes(termo) || u.login.toLowerCase().includes(termo));
    if (selectNivel.value) lista = lista.filter((u) => u.nivelAcesso === selectNivel.value);
    lista.sort((a, b) => a.nomeUsuario.localeCompare(b.nomeUsuario));

    if (lista.length === 0) {
      tbody.innerHTML = '';
      estadoVazio.innerHTML = `<div class="empty-state"><strong>Nenhum usuário encontrado</strong>Ajuste os filtros ou cadastre um novo usuário.</div>`;
      return;
    }
    estadoVazio.innerHTML = '';
    tbody.innerHTML = lista.map((u) => `
      <tr>
        <td><strong>${EstoqueUtils.escapeHtml(u.nomeUsuario)}</strong></td>
        <td class="mono">${EstoqueUtils.escapeHtml(u.login)}</td>
        <td><span class="tag ${tagClasse[u.nivelAcesso] || ''}">${u.nivelAcesso}</span></td>
        <td>
          <div class="row-actions">
            <button class="icon-btn" data-editar="${u.idUsuario}" title="Editar">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 20h9"/><path d="M16.5 3.5a2.1 2.1 0 013 3L7 19l-4 1 1-4 12.5-12.5z"/></svg>
            </button>
            <button class="icon-btn" data-excluir="${u.idUsuario}" data-nome="${EstoqueUtils.escapeHtml(u.nomeUsuario)}" title="Excluir" ${u.idUsuario === sessao.idUsuario ? 'disabled' : ''}>
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 6h18"/><path d="M8 6V4h8v2"/><path d="M19 6l-1 14H6L5 6"/></svg>
            </button>
          </div>
        </td>
      </tr>`).join('');
  }

  document.getElementById('btn-novo').addEventListener('click', () => {
    form.reset();
    EstoqueUtils.limparValidacao(form);
    form.idUsuario.value = '';
    document.getElementById('modal-titulo').textContent = 'Novo usuário';
    modalForm.classList.add('is-open');
  });

  tbody.addEventListener('click', (e) => {
    const btnEditar = e.target.closest('[data-editar]');
    if (btnEditar) {
      const u = usuariosCache.find((u) => u.idUsuario === Number(btnEditar.dataset.editar));
      form.reset();
      EstoqueUtils.limparValidacao(form);
      form.idUsuario.value = u.idUsuario;
      form.nomeUsuario.value = u.nomeUsuario;
      form.login.value = u.login;
      form.senha.value = ''; // o backend nunca devolve a senha - ver Usuario.java (@JsonProperty WRITE_ONLY)
      form.nivelAcesso.value = u.nivelAcesso;
      document.getElementById('modal-titulo').textContent = 'Editar usuário';
      modalForm.classList.add('is-open');
      return;
    }
    const btnExcluir = e.target.closest('[data-excluir]');
    if (btnExcluir && !btnExcluir.disabled) {
      idParaExcluir = btnExcluir.dataset.excluir;
      document.getElementById('texto-confirmacao').textContent = `Tem certeza que deseja excluir o usuário "${btnExcluir.dataset.nome}"?`;
      modalExcluir.classList.add('is-open');
    }
  });

  document.querySelectorAll('[data-close-modal]').forEach((el) => {
    el.addEventListener('click', () => { modalForm.classList.remove('is-open'); modalExcluir.classList.remove('is-open'); });
  });

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    const eEdicao = !!form.idUsuario.value;
    const regras = {
      nomeUsuario: [EstoqueUtils.validadores.obrigatorio('Informe o nome completo.'), EstoqueUtils.validadores.minLen(3)],
      login: [EstoqueUtils.validadores.obrigatorio('Informe o login.')],
    };
    // Na edição, a senha pode ficar em branco (mantém a atual) - só valida
    // o tamanho mínimo se algo for digitado. No cadastro, é obrigatória.
    if (!eEdicao) {
      regras.senha = [EstoqueUtils.validadores.obrigatorio('Informe a senha.'), EstoqueUtils.validadores.minLen(6)];
    } else if (form.senha.value) {
      regras.senha = [EstoqueUtils.validadores.minLen(6)];
    }
    if (!EstoqueUtils.validarFormulario(form, regras)) return;

    const dados = {
      nomeUsuario: form.nomeUsuario.value.trim(),
      login: form.login.value.trim(),
      senha: form.senha.value,
      nivelAcesso: form.nivelAcesso.value,
    };

    try {
      if (eEdicao) {
        await EstoqueUtils.comCarregamento(EstoqueAPI.usuarios.atualizar(form.idUsuario.value, dados));
        EstoqueUtils.toast('Usuário atualizado.');
      } else {
        await EstoqueUtils.comCarregamento(EstoqueAPI.usuarios.salvar(dados));
        EstoqueUtils.toast('Usuário cadastrado.');
      }
      modalForm.classList.remove('is-open');
      await carregar();
      render();
    } catch (erro) {
      // Ex.: "Ja existe um usuario com este login." (UsuarioService, Etapa 9)
      const fieldWrap = form.login.closest('.field');
      fieldWrap.classList.add('has-error');
      fieldWrap.querySelector('.field__error').textContent = erro.message || 'Não foi possível salvar.';
      EstoqueUtils.toast(erro.message || 'Não foi possível salvar o usuário.', 'erro');
    }
  });

  document.getElementById('btn-confirmar-exclusao').addEventListener('click', async () => {
    try {
      await EstoqueUtils.comCarregamento(EstoqueAPI.usuarios.remover(idParaExcluir));
      modalExcluir.classList.remove('is-open');
      EstoqueUtils.toast('Usuário excluído.');
      await carregar();
      render();
    } catch (erro) {
      modalExcluir.classList.remove('is-open');
      EstoqueUtils.toast(erro.message || 'Não foi possível excluir o usuário.', 'erro');
    }
  });

  inputBusca.addEventListener('input', EstoqueUtils.debounce(render, 150));
  selectNivel.addEventListener('change', render);

  try {
    await EstoqueUtils.comCarregamento(carregar());
    render();
  } catch (erro) {
    EstoqueUtils.toast(erro.message || 'Não foi possível carregar os usuários.', 'erro');
  }
});
