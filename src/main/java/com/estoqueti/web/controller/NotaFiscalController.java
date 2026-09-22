package com.estoqueti.web.controller;

import com.estoqueti.core.exception.RegraNegocioException;
import com.estoqueti.core.model.NotaFiscal;
import com.estoqueti.core.service.NotaFiscalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notas-fiscais")
public class NotaFiscalController {

    private final NotaFiscalService notaFiscalService;

    public NotaFiscalController(NotaFiscalService notaFiscalService) {
        this.notaFiscalService = notaFiscalService;
    }

    @GetMapping
    public List<NotaFiscal> listar() {
        return notaFiscalService.listarTodas();
    }

    @GetMapping("/{id}")
    public NotaFiscal buscarPorId(@PathVariable int id) {
        return notaFiscalService.buscarPorId(id)
                .orElseThrow(() -> new RegraNegocioException("Nota fiscal id=" + id + " nao encontrada."));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NotaFiscal cadastrar(@RequestBody NotaFiscal notaFiscal) {
        return notaFiscalService.cadastrar(notaFiscal);
    }

    @PutMapping("/{id}")
    public NotaFiscal atualizar(@PathVariable int id, @RequestBody NotaFiscal notaFiscal) {
        notaFiscal.setIdNf(id);
        notaFiscalService.atualizar(notaFiscal);
        return notaFiscal;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable int id) {
        notaFiscalService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
