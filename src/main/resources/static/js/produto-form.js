document.addEventListener('DOMContentLoaded', async () => {
  const params = new URLSearchParams(window.location.search);
  const id = params.get('id');
  const form = document.getElementById('form-produto');
  const selectCategoria = document.getElementById('idCategoria');
  const hintMargem = document.getElementById('hint-margem');

  try {
    const categorias = await EstoqueUtils.comCarregamento(EstoqueAPI.categorias.listar());
    categorias.forEach((c) => {
      const opt = document.createElement('option');
      opt.value = c.idCategoria;
      opt.textContent = c.nomeCategoria;
      selectCategoria.appendChild(opt);
    });
  } catch (erro) {
    EstoqueUtils.toast(erro.message || 'Não foi possível carregar as categorias.', 'erro');
  }

  let produtoEditando = null;
  if (id) {
    try {
      produtoEditando = await EstoqueUtils.comCarregamento(EstoqueAPI.produtos.buscarPorId(id));
      document.getElementById('titulo-pagina').textContent = 'Editar produto';
      document.getElementById('crumb-titulo').textContent = produtoEditando.nomeProduto;
      document.title = `Editar ${produtoEditando.nomeProduto} · EstoqueTI`;
      Object.keys(produtoEditando).forEach((campo) => {
        const el = form.querySelector(`[name="${campo}"]`);
        if (el) el.value = produtoEditando[campo];
      });
    } catch (erro) {
      EstoqueUtils.toast(erro.message || 'Produto não encontrado.', 'erro');
    }
  }

  function atualizarMargem() {
    const custo = parseFloat(form.precoCusto.value);
    const venda = parseFloat(form.precoVenda.value);
    if (isNaN(custo) || isNaN(venda) || custo <= 0) {
      hintMargem.textContent = 'Margem de lucro estimada: —';
      return;
    }
    const margem = ((venda - custo) / custo) * 100;
    hintMargem.textContent = `Margem de lucro estimada: ${margem.toFixed(2)}% (lucro de ${EstoqueUtils.formatarMoeda(venda - custo)} por unidade)`;
  }
  form.precoCusto.addEventListener('input', atualizarMargem);
  form.precoVenda.addEventListener('input', atualizarMargem);
  atualizarMargem();

  const V = EstoqueUtils.validadores;
  const regras = {
    nomeProduto: [V.obrigatorio('Informe o nome do produto.'), V.minLen(3)],
    marca: [V.obrigatorio('Informe a marca.')],
    idCategoria: [V.obrigatorio('Selecione uma categoria.')],
    precoCusto: [V.obrigatorio('Informe o preço de custo.'), V.numeroNaoNegativo()],
    precoVenda: [V.obrigatorio('Informe o preço de venda.'), V.numeroPositivo()],
    quantidade: [V.obrigatorio('Informe a quantidade.'), V.numeroNaoNegativo()],
  };

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    if (!EstoqueUtils.validarFormulario(form, regras)) {
      EstoqueUtils.toast('Verifique os campos destacados no formulário.', 'erro');
      return;
    }

    if (Number(form.precoVenda.value) < Number(form.precoCusto.value)) {
      const fieldWrap = form.precoVenda.closest('.field');
      fieldWrap.classList.add('has-error');
      fieldWrap.querySelector('.field__error').textContent = 'O preço de venda não deve ser menor que o preço de custo.';
      EstoqueUtils.toast('O preço de venda está menor que o custo. Revise os valores.', 'erro');
      return;
    }

    const dados = {
      nomeProduto: form.nomeProduto.value.trim(),
      descricao: form.descricao.value.trim(),
      unidadeMedida: form.unidadeMedida.value,
      idCategoria: Number(form.idCategoria.value),
      marca: form.marca.value.trim(),
      precoCusto: Number(form.precoCusto.value),
      precoVenda: Number(form.precoVenda.value),
      quantidade: Number(form.quantidade.value),
      status: form.status.value,
    };

    try {
      if (produtoEditando) {
        await EstoqueUtils.comCarregamento(EstoqueAPI.produtos.atualizar(produtoEditando.idProduto, dados));
        EstoqueUtils.toast('Produto atualizado com sucesso.');
      } else {
        await EstoqueUtils.comCarregamento(EstoqueAPI.produtos.salvar(dados));
        EstoqueUtils.toast('Produto cadastrado com sucesso.');
      }
      setTimeout(() => { window.location.href = 'produtos.html'; }, 400);
    } catch (erro) {
      // Regras de negocio do backend (ex.: categoria invalida, precos
      // negativos) chegam aqui como RegraNegocioException -> 400 -> toast.
      EstoqueUtils.toast(erro.message || 'Não foi possível salvar o produto.', 'erro');
    }
  });
});
