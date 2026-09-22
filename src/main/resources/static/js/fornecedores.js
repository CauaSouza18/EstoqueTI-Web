document.addEventListener('DOMContentLoaded', async () => {
  const tbody = document.getElementById('tbody-fornecedores');
  const estadoVazio = document.getElementById('estado-vazio');
  const inputBusca = document.getElementById('busca');
  const selectStatus = document.getElementById('filtro-status');
  const modalForm = document.getElementById('modal-form');
  const modalExcluir = document.getElementById('modal-excluir');
  const form = document.getElementById('form-fornecedor');
  let idParaExcluir = null;
  let fornecedoresCache = [];

  document.getElementById('cnpj').addEventListener('input', (e) => { e.target.value = EstoqueUtils.maskCnpj(e.target.value); });
  document.getElementById('telefone').addEventListener('input', (e) => { e.target.value = EstoqueUtils.maskTelefone(e.target.value); });

  async function carregar() {
    fornecedoresCache = await EstoqueAPI.fornecedores.listar();
  }

  function render() {
    let lista = [...fornecedoresCache];
    const termo = inputBusca.value.trim().toLowerCase();
    if (termo) lista = lista.filter((f) => f.nomeFornecedor.toLowerCase().includes(termo) || f.cnpj.includes(termo));
    if (selectStatus.value) lista = lista.filter((f) => f.status === selectStatus.value);
    lista.sort((a, b) => a.nomeFornecedor.localeCompare(b.nomeFornecedor));

    if (lista.length === 0) {
      tbody.innerHTML = '';
      estadoVazio.innerHTML = `<div class="empty-state"><strong>Nenhum fornecedor encontrado</strong>Ajuste os filtros ou cadastre um novo fornecedor.</div>`;
      return;
    }
    estadoVazio.innerHTML = '';
    tbody.innerHTML = lista.map((f) => `
      <tr>
        <td><strong>${EstoqueUtils.escapeHtml(f.nomeFornecedor)}</strong><br><span style="font-size:11px;color:var(--steel-400);">${EstoqueUtils.escapeHtml(f.endereco || '')}</span></td>
        <td class="mono">${EstoqueUtils.escapeHtml(f.cnpj)}</td>
        <td>${EstoqueUtils.escapeHtml(f.telefone)}<br><span style="font-size:11px;color:var(--steel-400);">${EstoqueUtils.escapeHtml(f.email)}</span></td>
        <td><span class="tag ${f.status === 'Ativo' ? 'tag--ativo' : 'tag--inativo'}">${f.status}</span></td>
        <td>
          <div class="row-actions">
            <button class="icon-btn" data-editar="${f.idFornecedor}" title="Editar">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 20h9"/><path d="M16.5 3.5a2.1 2.1 0 013 3L7 19l-4 1 1-4 12.5-12.5z"/></svg>
            </button>
            <button class="icon-btn" data-excluir="${f.idFornecedor}" data-nome="${EstoqueUtils.escapeHtml(f.nomeFornecedor)}" title="Excluir">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 6h18"/><path d="M8 6V4h8v2"/><path d="M19 6l-1 14H6L5 6"/></svg>
            </button>
          </div>
        </td>
      </tr>`).join('');
  }

  document.getElementById('btn-novo').addEventListener('click', () => {
    form.reset();
    EstoqueUtils.limparValidacao(form);
    form.idFornecedor.value = '';
    form.status.value = 'Ativo';
    document.getElementById('modal-titulo').textContent = 'Novo fornecedor';
    modalForm.classList.add('is-open');
  });

  tbody.addEventListener('click', (e) => {
    const btnEditar = e.target.closest('[data-editar]');
    if (btnEditar) {
      const f = fornecedoresCache.find((f) => f.idFornecedor === Number(btnEditar.dataset.editar));
      form.reset();
      EstoqueUtils.limparValidacao(form);
      Object.keys(f).forEach((campo) => { if (form[campo]) form[campo].value = f[campo]; });
      document.getElementById('modal-titulo').textContent = 'Editar fornecedor';
      modalForm.classList.add('is-open');
      return;
    }
    const btnExcluir = e.target.closest('[data-excluir]');
    if (btnExcluir) {
      idParaExcluir = btnExcluir.dataset.excluir;
      document.getElementById('texto-confirmacao').textContent = `Tem certeza que deseja excluir "${btnExcluir.dataset.nome}"?`;
      modalExcluir.classList.add('is-open');
    }
  });

  document.querySelectorAll('[data-close-modal]').forEach((el) => {
    el.addEventListener('click', () => { modalForm.classList.remove('is-open'); modalExcluir.classList.remove('is-open'); });
  });

  const V = EstoqueUtils.validadores;
  const regras = {
    nomeFornecedor: [V.obrigatorio('Informe a razão social.'), V.minLen(3)],
    cnpj: [V.obrigatorio('Informe o CNPJ.'), V.cnpj()],
    telefone: [V.obrigatorio('Informe o telefone.'), V.telefone()],
    email: [V.obrigatorio('Informe o e-mail.'), V.email()],
  };

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    if (!EstoqueUtils.validarFormulario(form, regras)) return;
    const dados = {
      nomeFornecedor: form.nomeFornecedor.value.trim(),
      cnpj: form.cnpj.value.trim(),
      telefone: form.telefone.value.trim(),
      email: form.email.value.trim(),
      endereco: form.endereco.value.trim(),
      status: form.status.value,
    };
    try {
      if (form.idFornecedor.value) {
        await EstoqueUtils.comCarregamento(EstoqueAPI.fornecedores.atualizar(form.idFornecedor.value, dados));
        EstoqueUtils.toast('Fornecedor atualizado.');
      } else {
        await EstoqueUtils.comCarregamento(EstoqueAPI.fornecedores.salvar(dados));
        EstoqueUtils.toast('Fornecedor cadastrado.');
      }
      modalForm.classList.remove('is-open');
      await carregar();
      render();
    } catch (erro) {
      EstoqueUtils.toast(erro.message || 'Não foi possível salvar o fornecedor.', 'erro');
    }
  });

  document.getElementById('btn-confirmar-exclusao').addEventListener('click', async () => {
    try {
      await EstoqueUtils.comCarregamento(EstoqueAPI.fornecedores.remover(idParaExcluir));
      modalExcluir.classList.remove('is-open');
      EstoqueUtils.toast('Fornecedor excluído.');
      await carregar();
      render();
    } catch (erro) {
      modalExcluir.classList.remove('is-open');
      EstoqueUtils.toast(erro.message || 'Não foi possível excluir o fornecedor.', 'erro');
    }
  });

  inputBusca.addEventListener('input', EstoqueUtils.debounce(render, 150));
  selectStatus.addEventListener('change', render);

  try {
    await EstoqueUtils.comCarregamento(carregar());
    render();
  } catch (erro) {
    EstoqueUtils.toast(erro.message || 'Não foi possível carregar os fornecedores.', 'erro');
  }
});
