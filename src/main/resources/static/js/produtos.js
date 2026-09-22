document.addEventListener('DOMContentLoaded', async () => {
  const selectCategoria = document.getElementById('filtro-categoria');
  const inputBusca = document.getElementById('busca');
  const selectStatus = document.getElementById('filtro-status');
  const tbody = document.getElementById('tbody-produtos');
  const estadoVazio = document.getElementById('estado-vazio-produtos');
  const infoPaginacao = document.getElementById('info-paginacao');
  const modalExcluir = document.getElementById('modal-excluir');
  const btnConfirmarExclusao = document.getElementById('btn-confirmar-exclusao');
  let idParaExcluir = null;
  let ordenacao = { campo: 'nomeProduto', dir: 1 };
  let categorias = [];
  let produtosCache = [];

  function nomeCategoria(idCategoria) {
    const c = categorias.find((c) => c.idCategoria === idCategoria);
    return c ? c.nomeCategoria : '—';
  }

  async function carregarCategorias() {
    categorias = await EstoqueAPI.categorias.listar();
    selectCategoria.innerHTML = '<option value="">Todas as categorias</option>';
    categorias.forEach((c) => {
      const opt = document.createElement('option');
      opt.value = c.idCategoria;
      opt.textContent = c.nomeCategoria;
      selectCategoria.appendChild(opt);
    });
  }

  async function carregarProdutos() {
    produtosCache = await EstoqueAPI.produtos.listar();
  }

  function render() {
    let produtos = [...produtosCache];

    const termo = inputBusca.value.trim().toLowerCase();
    if (termo) {
      produtos = produtos.filter((p) =>
        p.nomeProduto.toLowerCase().includes(termo) || p.marca.toLowerCase().includes(termo));
    }
    if (selectCategoria.value) produtos = produtos.filter((p) => p.idCategoria === Number(selectCategoria.value));
    if (selectStatus.value) produtos = produtos.filter((p) => p.status === selectStatus.value);

    produtos.sort((a, b) => {
      const va = a[ordenacao.campo], vb = b[ordenacao.campo];
      if (typeof va === 'string') return va.localeCompare(vb) * ordenacao.dir;
      return (va - vb) * ordenacao.dir;
    });

    document.querySelectorAll('.data-table thead th[data-sort]').forEach((th) => {
      th.classList.toggle('is-sorted', th.dataset.sort === ordenacao.campo);
    });

    if (produtos.length === 0) {
      tbody.innerHTML = '';
      estadoVazio.innerHTML = `
        <div class="empty-state">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M21 8l-9-5-9 5 9 5 9-5z"/><path d="M3 8v8l9 5 9-5V8"/></svg>
          <strong>Nenhum produto encontrado</strong>
          Ajuste os filtros ou cadastre um novo produto.
        </div>`;
    } else {
      estadoVazio.innerHTML = '';
      tbody.innerHTML = produtos.map((p) => `
        <tr>
          <td><strong>${EstoqueUtils.escapeHtml(p.nomeProduto)}</strong><br><span class="mono" style="font-size:11px;color:var(--steel-400);">${EstoqueUtils.escapeHtml(p.unidadeMedida)}</span></td>
          <td>${EstoqueUtils.escapeHtml(p.marca)}</td>
          <td><span class="tag tag--info">${EstoqueUtils.escapeHtml(nomeCategoria(p.idCategoria))}</span></td>
          <td class="num">${EstoqueUtils.formatarMoeda(p.precoVenda)}</td>
          <td class="num">${p.quantidade <= 5 ? `<span class="tag ${p.quantidade === 0 ? 'tag--inativo' : 'tag--baixo'}">${p.quantidade}</span>` : p.quantidade}</td>
          <td><span class="tag ${p.status === 'Ativo' ? 'tag--ativo' : 'tag--inativo'}">${p.status}</span></td>
          <td>
            <div class="row-actions">
              <a class="icon-btn" href="produto-form.html?id=${p.idProduto}" title="Editar">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 20h9"/><path d="M16.5 3.5a2.1 2.1 0 013 3L7 19l-4 1 1-4 12.5-12.5z"/></svg>
              </a>
              <button class="icon-btn" data-excluir="${p.idProduto}" data-nome="${EstoqueUtils.escapeHtml(p.nomeProduto)}" title="Excluir">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 6h18"/><path d="M8 6V4h8v2"/><path d="M19 6l-1 14H6L5 6"/></svg>
              </button>
            </div>
          </td>
        </tr>`).join('');
    }

    infoPaginacao.textContent = `${produtos.length} produto(s) encontrado(s)`;
  }

  document.querySelectorAll('.data-table thead th[data-sort]').forEach((th) => {
    th.addEventListener('click', () => {
      const campo = th.dataset.sort;
      ordenacao.dir = ordenacao.campo === campo ? ordenacao.dir * -1 : 1;
      ordenacao.campo = campo;
      render();
    });
  });

  inputBusca.addEventListener('input', EstoqueUtils.debounce(render, 150));
  selectCategoria.addEventListener('change', render);
  selectStatus.addEventListener('change', render);

  tbody.addEventListener('click', (e) => {
    const btn = e.target.closest('[data-excluir]');
    if (!btn) return;
    idParaExcluir = btn.dataset.excluir;
    document.getElementById('texto-confirmacao-exclusao').textContent =
      `Tem certeza que deseja excluir "${btn.dataset.nome}"? Essa ação não pode ser desfeita.`;
    modalExcluir.classList.add('is-open');
  });

  document.querySelectorAll('[data-close-modal]').forEach((el) => {
    el.addEventListener('click', () => modalExcluir.classList.remove('is-open'));
  });

  btnConfirmarExclusao.addEventListener('click', async () => {
    try {
      await EstoqueUtils.comCarregamento(EstoqueAPI.produtos.remover(idParaExcluir));
      modalExcluir.classList.remove('is-open');
      EstoqueUtils.toast('Produto excluído com sucesso.');
      await carregarProdutos();
      render();
    } catch (erro) {
      modalExcluir.classList.remove('is-open');
      EstoqueUtils.toast(erro.message || 'Não foi possível excluir o produto.', 'erro');
    }
  });

  try {
    await EstoqueUtils.comCarregamento(Promise.all([carregarCategorias(), carregarProdutos()]));
    render();
  } catch (erro) {
    EstoqueUtils.toast(erro.message || 'Não foi possível carregar os produtos.', 'erro');
  }
});
