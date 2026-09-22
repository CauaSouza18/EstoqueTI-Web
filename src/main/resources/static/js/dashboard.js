const LIMITE_ESTOQUE_BAIXO = 5;

document.addEventListener('DOMContentLoaded', async () => {
  let produtos, movimentacoes, fornecedores;
  try {
    [produtos, movimentacoes, fornecedores] = await EstoqueUtils.comCarregamento(Promise.all([
      EstoqueAPI.produtos.listar(),
      EstoqueAPI.movimentacoes.listarRecentes(6),
      EstoqueAPI.fornecedores.listar(),
    ]));
  } catch (erro) {
    EstoqueUtils.toast(erro.message || 'Não foi possível carregar o painel.', 'erro');
    return;
  }

  const totalProdutosAtivos = produtos.filter((p) => p.status === 'Ativo').length;
  const estoqueBaixo = produtos.filter((p) => p.status === 'Ativo' && p.quantidade <= LIMITE_ESTOQUE_BAIXO);
  const valorTotalEstoque = produtos.reduce((sum, p) => sum + p.precoVenda * p.quantidade, 0);
  const fornecedoresAtivos = fornecedores.filter((f) => f.status === 'Ativo').length;

  document.getElementById('cards-resumo').innerHTML = `
    <div class="stat-card stat-card--amber">
      <div class="stat-card__label">Produtos ativos</div>
      <div class="stat-card__value mono">${totalProdutosAtivos}</div>
      <div class="stat-card__hint">de ${produtos.length} cadastrados</div>
    </div>
    <div class="stat-card stat-card--rose">
      <div class="stat-card__label">Estoque baixo</div>
      <div class="stat-card__value mono">${estoqueBaixo.length}</div>
      <div class="stat-card__hint">quantidade ≤ ${LIMITE_ESTOQUE_BAIXO} unidades</div>
    </div>
    <div class="stat-card stat-card--teal">
      <div class="stat-card__label">Valor em estoque</div>
      <div class="stat-card__value mono" style="font-size: 1.6rem;">${EstoqueUtils.formatarMoeda(valorTotalEstoque)}</div>
      <div class="stat-card__hint">a preço de venda</div>
    </div>
    <div class="stat-card stat-card--blue">
      <div class="stat-card__label">Fornecedores ativos</div>
      <div class="stat-card__value mono">${fornecedoresAtivos}</div>
      <div class="stat-card__hint">de ${fornecedores.length} cadastrados</div>
    </div>
  `;

  const listaMov = document.getElementById('lista-movimentacoes');
  if (movimentacoes.length === 0) {
    listaMov.innerHTML = `<div class="empty-state"><strong>Nenhuma movimentação</strong>Os lançamentos aparecerão aqui.</div>`;
  } else {
    listaMov.innerHTML = movimentacoes.map((m) => {
      const produto = produtos.find((p) => p.idProduto === m.idProduto);
      const entrada = m.tipoMovimentacao === 'Entrada';
      return `
        <div class="mov-item">
          <div class="mov-item__icon ${entrada ? 'entrada' : 'saida'}">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              ${entrada ? '<path d="M12 19V5M5 12l7-7 7 7"/>' : '<path d="M12 5v14M5 12l7 7 7-7"/>'}
            </svg>
          </div>
          <div class="mov-item__text">
            <strong>${EstoqueUtils.escapeHtml(produto ? produto.nomeProduto : 'Produto removido')}</strong>
            <span>${EstoqueUtils.formatarDataHora(m.dataMovimentacao)}</span>
          </div>
          <div class="mov-item__qty ${entrada ? 'entrada' : 'saida'}">${entrada ? '+' : '-'}${m.quantidade}</div>
        </div>`;
    }).join('');
  }

  const listaBaixo = document.getElementById('lista-estoque-baixo');
  if (estoqueBaixo.length === 0) {
    listaBaixo.innerHTML = `<div class="empty-state"><strong>Tudo certo por aqui</strong>Nenhum produto abaixo do limite.</div>`;
  } else {
    listaBaixo.innerHTML = estoqueBaixo.map((p) => `
      <div class="low-stock-item">
        <div class="low-stock-item__name">${EstoqueUtils.escapeHtml(p.nomeProduto)}<span>${EstoqueUtils.escapeHtml(p.marca)}</span></div>
        <span class="tag ${p.quantidade === 0 ? 'tag--inativo' : 'tag--baixo'}">${p.quantidade} un.</span>
      </div>`).join('');
  }
});
