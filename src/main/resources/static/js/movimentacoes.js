document.addEventListener('DOMContentLoaded', async () => {
  const tbody = document.getElementById('tbody-movimentacoes');
  const estadoVazio = document.getElementById('estado-vazio');
  const inputBusca = document.getElementById('busca');
  const selectTipo = document.getElementById('filtro-tipo');
  const selectProduto = document.getElementById('idProduto');
  const hintEstoque = document.getElementById('hint-estoque-atual');
  const form = document.getElementById('form-movimentacao');
  const sessao = EstoqueAuth.sessaoAtual();

  let produtosCache = [];
  let movimentacoesCache = [];

  async function carregarProdutos() {
    produtosCache = await EstoqueAPI.produtos.listar();
    selectProduto.innerHTML = '<option value="">Selecione...</option>';
    produtosCache.filter((p) => p.status === 'Ativo').forEach((p) => {
      const opt = document.createElement('option');
      opt.value = p.idProduto;
      opt.textContent = `${p.nomeProduto} (${p.quantidade} ${p.unidadeMedida} em estoque)`;
      selectProduto.appendChild(opt);
    });
  }

  async function carregarMovimentacoes() {
    movimentacoesCache = await EstoqueAPI.movimentacoes.listar();
  }

  selectProduto.addEventListener('change', () => {
    const produto = produtosCache.find((p) => p.idProduto === Number(selectProduto.value));
    hintEstoque.textContent = produto ? `Estoque atual: ${produto.quantidade} ${produto.unidadeMedida}` : '—';
  });

  function render() {
    let lista = [...movimentacoesCache].sort((a, b) => new Date(b.dataMovimentacao) - new Date(a.dataMovimentacao));
    const termo = inputBusca.value.trim().toLowerCase();

    if (termo) {
      lista = lista.filter((m) => {
        const p = produtosCache.find((p) => p.idProduto === m.idProduto);
        return p && p.nomeProduto.toLowerCase().includes(termo);
      });
    }
    if (selectTipo.value) lista = lista.filter((m) => m.tipoMovimentacao === selectTipo.value);

    if (lista.length === 0) {
      tbody.innerHTML = '';
      estadoVazio.innerHTML = `<div class="empty-state"><strong>Nenhuma movimentação encontrada</strong>Lance uma entrada ou saída pelo formulário ao lado.</div>`;
      return;
    }
    estadoVazio.innerHTML = '';
    tbody.innerHTML = lista.map((m) => {
      const p = produtosCache.find((p) => p.idProduto === m.idProduto);
      const entrada = m.tipoMovimentacao === 'Entrada';
      return `
        <tr>
          <td class="mono">${EstoqueUtils.formatarDataHora(m.dataMovimentacao)}</td>
          <td>${EstoqueUtils.escapeHtml(p ? p.nomeProduto : 'Produto removido')}</td>
          <td><span class="tag ${entrada ? 'tag--entrada' : 'tag--saida'}">${m.tipoMovimentacao}</span></td>
          <td class="num mono" style="color:${entrada ? 'var(--teal-entrada)' : 'var(--rose-saida)'};">${entrada ? '+' : '-'}${m.quantidade}</td>
          <td style="color:var(--steel-400); font-size:var(--fs-sm);">${EstoqueUtils.escapeHtml(m.observacao || '—')}</td>
        </tr>`;
    }).join('');
  }

  const V = EstoqueUtils.validadores;
  const regras = {
    idProduto: [V.obrigatorio('Selecione um produto.')],
    quantidade: [V.obrigatorio('Informe a quantidade.'), V.numeroPositivo()],
  };

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    if (!EstoqueUtils.validarFormulario(form, regras)) return;

    const dados = {
      idProduto: Number(form.idProduto.value),
      tipoMovimentacao: form.tipoMovimentacao.value,
      quantidade: Number(form.quantidade.value),
      observacao: form.observacao.value.trim(),
      idUsuario: sessao ? sessao.idUsuario : 0,
    };

    try {
      // MovimentacaoEstoqueService (Etapa 6, sem alteracoes) valida no
      // servidor se o usuario pode operar e se ha estoque suficiente para
      // uma saida - a mensagem de erro (ex.: "Estoque insuficiente...")
      // vem pronta do backend e cai direto no toast.
      await EstoqueUtils.comCarregamento(EstoqueAPI.movimentacoes.registrar(dados));
      form.reset();
      EstoqueUtils.limparValidacao(form);
      hintEstoque.textContent = '—';
      EstoqueUtils.toast(`${dados.tipoMovimentacao === 'Entrada' ? 'Entrada' : 'Saída'} de ${dados.quantidade} un. registrada com sucesso.`);
      await Promise.all([carregarProdutos(), carregarMovimentacoes()]);
      render();
    } catch (erro) {
      EstoqueUtils.toast(erro.message || 'Não foi possível registrar a movimentação.', 'erro');
    }
  });

  inputBusca.addEventListener('input', EstoqueUtils.debounce(render, 150));
  selectTipo.addEventListener('change', render);

  try {
    await EstoqueUtils.comCarregamento(Promise.all([carregarProdutos(), carregarMovimentacoes()]));
    render();
  } catch (erro) {
    EstoqueUtils.toast(erro.message || 'Não foi possível carregar as movimentações.', 'erro');
  }
});
