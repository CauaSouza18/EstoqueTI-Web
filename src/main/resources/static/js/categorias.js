document.addEventListener('DOMContentLoaded', async () => {
  const tbody = document.getElementById('tbody-categorias');
  const estadoVazio = document.getElementById('estado-vazio');
  const inputBusca = document.getElementById('busca');
  const modalForm = document.getElementById('modal-form');
  const modalExcluir = document.getElementById('modal-excluir');
  const form = document.getElementById('form-categoria');
  let idParaExcluir = null;
  let categoriasCache = [];
  let produtosCache = [];

  function contarProdutos(idCategoria) {
    return produtosCache.filter((p) => p.idCategoria === idCategoria).length;
  }

  async function carregar() {
    [categoriasCache, produtosCache] = await Promise.all([
      EstoqueAPI.categorias.listar(),
      EstoqueAPI.produtos.listar(),
    ]);
  }

  function render() {
    let categorias = [...categoriasCache];
    const termo = inputBusca.value.trim().toLowerCase();
    if (termo) categorias = categorias.filter((c) => c.nomeCategoria.toLowerCase().includes(termo));
    categorias.sort((a, b) => a.nomeCategoria.localeCompare(b.nomeCategoria));

    if (categorias.length === 0) {
      tbody.innerHTML = '';
      estadoVazio.innerHTML = `<div class="empty-state"><strong>Nenhuma categoria encontrada</strong>Cadastre uma nova categoria para organizar seus produtos.</div>`;
      return;
    }
    estadoVazio.innerHTML = '';
    tbody.innerHTML = categorias.map((c) => `
      <tr>
        <td><span class="tag tag--info">${EstoqueUtils.escapeHtml(c.nomeCategoria)}</span></td>
        <td class="num mono">${contarProdutos(c.idCategoria)}</td>
        <td>
          <div class="row-actions">
            <button class="icon-btn" data-editar="${c.idCategoria}" title="Editar">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 20h9"/><path d="M16.5 3.5a2.1 2.1 0 013 3L7 19l-4 1 1-4 12.5-12.5z"/></svg>
            </button>
            <button class="icon-btn" data-excluir="${c.idCategoria}" data-nome="${EstoqueUtils.escapeHtml(c.nomeCategoria)}" title="Excluir">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 6h18"/><path d="M8 6V4h8v2"/><path d="M19 6l-1 14H6L5 6"/></svg>
            </button>
          </div>
        </td>
      </tr>`).join('');
  }

  function abrirModalNova() {
    form.reset();
    EstoqueUtils.limparValidacao(form);
    form.idCategoria.value = '';
    document.getElementById('modal-titulo').textContent = 'Nova categoria';
    modalForm.classList.add('is-open');
    document.getElementById('nomeCategoria').focus();
  }

  document.getElementById('btn-nova-categoria').addEventListener('click', abrirModalNova);

  tbody.addEventListener('click', (e) => {
    const btnEditar = e.target.closest('[data-editar]');
    if (btnEditar) {
      const cat = categoriasCache.find((c) => c.idCategoria === Number(btnEditar.dataset.editar));
      form.reset();
      EstoqueUtils.limparValidacao(form);
      form.idCategoria.value = cat.idCategoria;
      form.nomeCategoria.value = cat.nomeCategoria;
      document.getElementById('modal-titulo').textContent = 'Editar categoria';
      modalForm.classList.add('is-open');
      return;
    }
    const btnExcluir = e.target.closest('[data-excluir]');
    if (btnExcluir) {
      idParaExcluir = btnExcluir.dataset.excluir;
      const qtd = contarProdutos(Number(idParaExcluir));
      document.getElementById('texto-confirmacao').textContent = qtd > 0
        ? `Esta categoria possui ${qtd} produto(s) vinculado(s). Excluir mesmo assim "${btnExcluir.dataset.nome}"?`
        : `Tem certeza que deseja excluir "${btnExcluir.dataset.nome}"?`;
      modalExcluir.classList.add('is-open');
    }
  });

  document.querySelectorAll('[data-close-modal]').forEach((el) => {
    el.addEventListener('click', () => { modalForm.classList.remove('is-open'); modalExcluir.classList.remove('is-open'); });
  });

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    const regras = { nomeCategoria: [EstoqueUtils.validadores.obrigatorio('Informe o nome da categoria.'), EstoqueUtils.validadores.minLen(2)] };
    if (!EstoqueUtils.validarFormulario(form, regras)) return;

    const dados = { nomeCategoria: form.nomeCategoria.value.trim() };
    try {
      if (form.idCategoria.value) {
        await EstoqueUtils.comCarregamento(EstoqueAPI.categorias.atualizar(form.idCategoria.value, dados));
        EstoqueUtils.toast('Categoria atualizada.');
      } else {
        await EstoqueUtils.comCarregamento(EstoqueAPI.categorias.salvar(dados));
        EstoqueUtils.toast('Categoria cadastrada.');
      }
      modalForm.classList.remove('is-open');
      await carregar();
      render();
    } catch (erro) {
      EstoqueUtils.toast(erro.message || 'Não foi possível salvar a categoria.', 'erro');
    }
  });

  document.getElementById('btn-confirmar-exclusao').addEventListener('click', async () => {
    try {
      await EstoqueUtils.comCarregamento(EstoqueAPI.categorias.remover(idParaExcluir));
      modalExcluir.classList.remove('is-open');
      EstoqueUtils.toast('Categoria excluída.');
      await carregar();
      render();
    } catch (erro) {
      modalExcluir.classList.remove('is-open');
      EstoqueUtils.toast(erro.message || 'Não foi possível excluir a categoria.', 'erro');
    }
  });

  inputBusca.addEventListener('input', EstoqueUtils.debounce(render, 150));

  try {
    await EstoqueUtils.comCarregamento(carregar());
    render();
  } catch (erro) {
    EstoqueUtils.toast(erro.message || 'Não foi possível carregar as categorias.', 'erro');
  }
});
