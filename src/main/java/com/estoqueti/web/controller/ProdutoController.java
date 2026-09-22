package com.estoqueti.web.controller;

import com.estoqueti.core.exception.RegraNegocioException;
import com.estoqueti.core.model.Produto;
import com.estoqueti.core.service.ProdutoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    public List<Produto> listar() {
        return produtoService.listarTodos();
    }

    @GetMapping("/{id}")
    public Produto buscarPorId(@PathVariable int id) {
        return produtoService.buscarPorId(id)
                .orElseThrow(() -> new RegraNegocioException("Produto id=" + id + " nao encontrado."));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Produto cadastrar(@RequestBody Produto produto) {
        return produtoService.cadastrar(produto);
    }

    @PutMapping("/{id}")
    public Produto atualizar(@PathVariable int id, @RequestBody Produto produto) {
        produto.setIdProduto(id);
        produtoService.atualizar(produto);
        return produto;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable int id) {
        produtoService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
