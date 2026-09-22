document.addEventListener('DOMContentLoaded', async () => {
  const tbody = document.getElementById('tbody-notas');
  const estadoVazio = document.getElementById('estado-vazio');
  const inputBusca = document.getElementById('busca');
  const modalForm = document.getElementById('modal-form');
  const modalExcluir = document.getElementById('modal-excluir');
  const form = document.getElementById('form-nota');
  const selectFornecedor = document.getElementById('idFornecedor');
  let idParaExcluir = null;
  let notasCache = [];
  let fornecedoresCache = [];

  async function carregar() {
    [notasCache, fornecedoresCache] = await Promise.all([
      EstoqueAPI.notasFiscais.listar(),
      EstoqueAPI.fornecedores.listar(),
    ]);
    selectFornecedor.innerHTML = '<option value="">Selecione...</option>';
    fornecedoresCache.forEach((f) => {
      const opt = document.createElement('option');
      opt.value = f.idFornecedor;
      opt.textContent = f.nomeFornecedor;
      selectFornecedor.appendChild(opt);
    });
  }

  function nomeFornecedor(id) {
    const f = fornecedoresCache.find((f) => f.idFornecedor === id);
    return f ? f.nomeFornecedor : '—';
  }

  function render() {
    let lista = [...notasCache].sort((a, b) => new Date(b.dataEmissao) - new Date(a.dataEmissao));
    const termo = inputBusca.value.trim().toLowerCase();
    if (termo) lista = lista.filter((n) => n.numeroNf.toLowerCase().includes(termo));

    if (lista.length === 0) {
      tbody.innerHTML = '';
      estadoVazio.innerHTML = `<div class="empty-state"><strong>Nenhuma nota fiscal encontrada</strong>Cadastre uma nova nota de recebimento.</div>`;
      return;
    }
    estadoVazio.innerHTML = '';
    tbody.innerHTML = lista.map((n) => `
      <tr>
        <td class="mono">Nº ${EstoqueUtils.escapeHtml(n.numeroNf)}</td>
        <td>${EstoqueUtils.escapeHtml(nomeFornecedor(n.idFornecedor))}</td>
        <td class="mono">${EstoqueUtils.formatarData(n.dataEmissao)}</td>
        <td class="num">${EstoqueUtils.formatarMoeda(n.valorTotal)}</td>
        <td>
          <div class="row-actions">
            <button class="icon-btn" data-editar="${n.idNf}" title="Editar">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 20h9"/><path d="M16.5 3.5a2.1 2.1 0 013 3L7 19l-4 1 1-4 12.5-12.5z"/></svg>
            </button>
            <button class="icon-btn" data-excluir="${n.idNf}" data-nome="${EstoqueUtils.escapeHtml(n.numeroNf)}" title="Excluir">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 6h18"/><path d="M8 6V4h8v2"/><path d="M19 6l-1 14H6L5 6"/></svg>
            </button>
          </div>
        </td>
      </tr>`).join('');
  }

  document.getElementById('btn-nova').addEventListener('click', () => {
    form.reset();
    EstoqueUtils.limparValidacao(form);
    form.idNf.value = '';
    form.dataEmissao.value = new Date().toISOString().slice(0, 10);
    document.getElementById('modal-titulo').textContent = 'Nova nota fiscal';
    modalForm.classList.add('is-open');
  });

  tbody.addEventListener('click', (e) => {
    const btnEditar = e.target.closest('[data-editar]');
    if (btnEditar) {
      const n = notasCache.find((n) => n.idNf === Number(btnEditar.dataset.editar));
      form.reset();
      EstoqueUtils.limparValidacao(form);
      form.idNf.value = n.idNf;
      form.numeroNf.value = n.numeroNf;
      form.dataEmissao.value = n.dataEmissao;
      form.idFornecedor.value = n.idFornecedor;
      form.valorTotal.value = n.valorTotal;
      document.getElementById('modal-titulo').textContent = 'Editar nota fiscal';
      modalForm.classList.add('is-open');
      return;
    }
    const btnExcluir = e.target.closest('[data-excluir]');
    if (btnExcluir) {
      idParaExcluir = btnExcluir.dataset.excluir;
      document.getElementById('texto-confirmacao').textContent = `Tem certeza que deseja excluir a NF nº ${btnExcluir.dataset.nome}?`;
      modalExcluir.classList.add('is-open');
    }
  });

  document.querySelectorAll('[data-close-modal]').forEach((el) => {
    el.addEventListener('click', () => { modalForm.classList.remove('is-open'); modalExcluir.classList.remove('is-open'); });
  });

  const V = EstoqueUtils.validadores;
  const regras = {
    numeroNf: [V.obrigatorio('Informe o número da nota fiscal.')],
    dataEmissao: [V.obrigatorio('Informe a data de emissão.')],
    idFornecedor: [V.obrigatorio('Selecione o fornecedor.')],
    valorTotal: [V.obrigatorio('Informe o valor total.'), V.numeroPositivo()],
  };

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    if (!EstoqueUtils.validarFormulario(form, regras)) return;
    const dados = {
      numeroNf: form.numeroNf.value.trim(),
      dataEmissao: form.dataEmissao.value,
      idFornecedor: Number(form.idFornecedor.value),
      valorTotal: Number(form.valorTotal.value),
    };
    try {
      if (form.idNf.value) {
        await EstoqueUtils.comCarregamento(EstoqueAPI.notasFiscais.atualizar(form.idNf.value, dados));
        EstoqueUtils.toast('Nota fiscal atualizada.');
      } else {
        await EstoqueUtils.comCarregamento(EstoqueAPI.notasFiscais.salvar(dados));
        EstoqueUtils.toast('Nota fiscal cadastrada.');
      }
      modalForm.classList.remove('is-open');
      await carregar();
      render();
    } catch (erro) {
      EstoqueUtils.toast(erro.message || 'Não foi possível salvar a nota fiscal.', 'erro');
    }
  });

  document.getElementById('btn-confirmar-exclusao').addEventListener('click', async () => {
    try {
      await EstoqueUtils.comCarregamento(EstoqueAPI.notasFiscais.remover(idParaExcluir));
      modalExcluir.classList.remove('is-open');
      EstoqueUtils.toast('Nota fiscal excluída.');
      await carregar();
      render();
    } catch (erro) {
      modalExcluir.classList.remove('is-open');
      EstoqueUtils.toast(erro.message || 'Não foi possível excluir a nota fiscal.', 'erro');
    }
  });

  inputBusca.addEventListener('input', EstoqueUtils.debounce(render, 150));

  try {
    await EstoqueUtils.comCarregamento(carregar());
    render();
  } catch (erro) {
    EstoqueUtils.toast(erro.message || 'Não foi possível carregar as notas fiscais.', 'erro');
  }
});
