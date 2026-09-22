package com.estoqueti.web.controller;

import com.estoqueti.core.exception.RegraNegocioException;
import com.estoqueti.core.model.MovimentacaoEstoque;
import com.estoqueti.core.model.Usuario;
import com.estoqueti.core.service.MovimentacaoEstoqueService;
import com.estoqueti.core.service.UsuarioService;
import com.estoqueti.web.dto.MovimentacaoRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movimentacoes")
public class MovimentacaoController {

    private final MovimentacaoEstoqueService movimentacaoService;
    private final UsuarioService usuarioService;

    public MovimentacaoController(MovimentacaoEstoqueService movimentacaoService, UsuarioService usuarioService) {
        this.movimentacaoService = movimentacaoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<MovimentacaoEstoque> listar() {
        return movimentacaoService.listarTodas();
    }

    @GetMapping("/recentes")
    public List<MovimentacaoEstoque> listarRecentes(@RequestParam(defaultValue = "6") int limite) {
        return movimentacaoService.listarUltimas(limite);
    }

    /**
     * Registra uma entrada ou saida de estoque. O usuario logado e
     * identificado por idUsuario no corpo da requisicao (o front-end
     * mantem a sessao do usuario autenticado e o envia em cada lancamento -
     * ver js/api.js). MovimentacaoEstoqueService.registrar() reaproveita,
     * sem alteracoes, a mesma validacao de permissao e estoque da Etapa 6.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MovimentacaoEstoque registrar(@RequestBody MovimentacaoRequest request) {
        Usuario usuarioLogado = usuarioService.buscarPorId(request.getIdUsuario())
                .orElseThrow(() -> new RegraNegocioException("Usuario logado invalido."));
        return movimentacaoService.registrar(
                usuarioLogado, request.getIdProduto(), request.getTipoMovimentacao(),
                request.getQuantidade(), request.getObservacao());
    }
}
