package com.estoqueti.web.controller;

import com.estoqueti.core.model.Fornecedor;
import com.estoqueti.core.service.FornecedorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fornecedores")
public class FornecedorController {

    private final FornecedorService fornecedorService;

    public FornecedorController(FornecedorService fornecedorService) {
        this.fornecedorService = fornecedorService;
    }

    @GetMapping
    public List<Fornecedor> listar() {
        return fornecedorService.listarTodos();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Fornecedor cadastrar(@RequestBody Fornecedor fornecedor) {
        return fornecedorService.cadastrar(fornecedor);
    }

    @PutMapping("/{id}")
    public Fornecedor atualizar(@PathVariable int id, @RequestBody Fornecedor fornecedor) {
        fornecedor.setIdFornecedor(id);
        fornecedorService.atualizar(fornecedor);
        return fornecedor;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable int id) {
        fornecedorService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
