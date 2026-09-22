package com.estoqueti.web.controller;

import com.estoqueti.core.model.Categoria;
import com.estoqueti.core.service.CategoriaService;
import com.estoqueti.web.dto.CategoriaRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public List<Categoria> listar() {
        return categoriaService.listarTodas();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Categoria cadastrar(@RequestBody CategoriaRequest request) {
        return categoriaService.cadastrar(request.getNomeCategoria());
    }

    @PutMapping("/{id}")
    public Categoria atualizar(@PathVariable int id, @RequestBody CategoriaRequest request) {
        return categoriaService.atualizar(id, request.getNomeCategoria());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable int id) {
        categoriaService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
