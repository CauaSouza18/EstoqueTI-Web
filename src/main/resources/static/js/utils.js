/**
 * utils.js — formatação, máscaras, validação de formulários, toast e
 * um indicador de carregamento (novo na Etapa 9: chamadas à API real
 * têm latência de rede, diferente do localStorage da Etapa 8).
 */
const EstoqueUtils = (function () {
  function formatarMoeda(valor) {
    return Number(valor || 0).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
  }

  function formatarData(isoOuData) {
    const d = typeof isoOuData === 'string' ? new Date(isoOuData) : isoOuData;
    return d.toLocaleDateString('pt-BR');
  }

  function formatarDataHora(isoOuData) {
    const d = typeof isoOuData === 'string' ? new Date(isoOuData) : isoOuData;
    return d.toLocaleDateString('pt-BR') + ' ' + d.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });
  }

  function escapeHtml(str) {
    return String(str ?? '').replace(/[&<>"']/g, (c) => ({
      '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;',
    }[c]));
  }

  function debounce(fn, delay) {
    let t;
    return (...args) => { clearTimeout(t); t = setTimeout(() => fn(...args), delay); };
  }

  function maskCnpj(value) {
    return value.replace(/\D/g, '').slice(0, 14)
      .replace(/^(\d{2})(\d)/, '$1.$2')
      .replace(/^(\d{2})\.(\d{3})(\d)/, '$1.$2.$3')
      .replace(/\.(\d{3})(\d)/, '.$1/$2')
      .replace(/(\d{4})(\d)/, '$1-$2');
  }

  function maskTelefone(value) {
    const digits = value.replace(/\D/g, '').slice(0, 11);
    if (digits.length <= 10) {
      return digits.replace(/^(\d{2})(\d)/, '($1) $2').replace(/(\d{4})(\d)/, '$1-$2');
    }
    return digits.replace(/^(\d{2})(\d)/, '($1) $2').replace(/(\d{5})(\d)/, '$1-$2');
  }

  function validarFormulario(form, regras) {
    let valido = true;
    Object.keys(regras).forEach((nomeCampo) => {
      const el = form.querySelector(`[name="${nomeCampo}"]`);
      if (!el) return;
      const fieldWrap = el.closest('.field') || el.parentElement;
      const erroEl = fieldWrap ? fieldWrap.querySelector('.field__error') : null;
      let erroCampo = null;

      for (const regra of regras[nomeCampo]) {
        if (!regra.teste(el.value.trim(), form)) { erroCampo = regra.msg; break; }
      }

      if (erroCampo) {
        valido = false;
        el.classList.add('is-invalid');
        if (fieldWrap) fieldWrap.classList.add('has-error');
        if (erroEl) erroEl.textContent = erroCampo;
      } else {
        el.classList.remove('is-invalid');
        if (fieldWrap) fieldWrap.classList.remove('has-error');
        if (erroEl) erroEl.textContent = '';
      }
    });
    return valido;
  }

  function limparValidacao(form) {
    form.querySelectorAll('.is-invalid').forEach((el) => el.classList.remove('is-invalid'));
    form.querySelectorAll('.has-error').forEach((el) => el.classList.remove('has-error'));
    form.querySelectorAll('.field__error').forEach((el) => { el.textContent = ''; });
  }

  const validadores = {
    obrigatorio: (msg = 'Campo obrigatório.') => ({ teste: (v) => v.length > 0, msg }),
    minLen: (n, msg) => ({ teste: (v) => v.length === 0 || v.length >= n, msg: msg || `Mínimo de ${n} caracteres.` }),
    numeroPositivo: (msg = 'Informe um número maior que zero.') => ({
      teste: (v) => v === '' || (!isNaN(v) && Number(v) > 0), msg,
    }),
    numeroNaoNegativo: (msg = 'Informe um número maior ou igual a zero.') => ({
      teste: (v) => v === '' || (!isNaN(v) && Number(v) >= 0), msg,
    }),
    email: (msg = 'Informe um e-mail válido.') => ({
      teste: (v) => v === '' || /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v), msg,
    }),
    cnpj: (msg = 'Informe um CNPJ válido (XX.XXX.XXX/XXXX-XX).') => ({
      teste: (v) => v === '' || /^\d{2}\.\d{3}\.\d{3}\/\d{4}-\d{2}$/.test(v), msg,
    }),
    telefone: (msg = 'Informe um telefone válido.') => ({
      teste: (v) => v === '' || /^\(\d{2}\)\s?\d{4,5}-\d{4}$/.test(v), msg,
    }),
  };

  function toast(mensagem, tipo = 'sucesso') {
    let region = document.getElementById('toast-region');
    if (!region) {
      region = document.createElement('div');
      region.id = 'toast-region';
      document.body.appendChild(region);
    }
    const el = document.createElement('div');
    el.className = 'toast' + (tipo === 'erro' ? ' toast--erro' : '');
    el.textContent = mensagem;
    region.appendChild(el);
    setTimeout(() => el.remove(), 3200);
  }

  function confirmar(mensagem) {
    return window.confirm(mensagem);
  }

  /* ---------------- Spinner de carregamento (novo na Etapa 9) ---------------- */
  function garantirSpinner() {
    let overlay = document.getElementById('spinner-overlay');
    if (!overlay) {
      overlay = document.createElement('div');
      overlay.id = 'spinner-overlay';
      overlay.className = 'spinner-overlay';
      overlay.innerHTML = '<span class="dot"></span><span class="dot"></span><span class="dot"></span>';
      document.body.appendChild(overlay);
    }
    return overlay;
  }

  function mostrarCarregando() { garantirSpinner().classList.add('is-open'); }
  function esconderCarregando() { garantirSpinner().classList.remove('is-open'); }

  /** Executa uma chamada assíncrona mostrando o spinner e tratando erro com toast. */
  async function comCarregamento(promessa) {
    mostrarCarregando();
    try {
      return await promessa;
    } finally {
      esconderCarregando();
    }
  }

  return {
    formatarMoeda, formatarData, formatarDataHora, escapeHtml, debounce,
    maskCnpj, maskTelefone,
    validarFormulario, limparValidacao, validadores,
    toast, confirmar,
    mostrarCarregando, esconderCarregando, comCarregamento,
  };
})();
