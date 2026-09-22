package com.estoqueti.web.controller;

import com.estoqueti.core.model.Usuario;
import com.estoqueti.core.service.AutenticacaoService;
import com.estoqueti.web.dto.LoginRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Expõe AutenticacaoService (Etapa 6, sem alteracoes) como endpoint REST.
 * Nao ha sessao no lado do servidor nesta etapa (escopo academico): o
 * front-end guarda o usuario retornado em localStorage apos o login (ver
 * js/auth.js) e o reenvia quando precisa (ex.: idUsuario ao lancar uma
 * movimentacao). O campo senha nunca volta no JSON (Usuario.senha e
 * @JsonProperty(WRITE_ONLY)).
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AutenticacaoService autenticacaoService;

    public AuthController(AutenticacaoService autenticacaoService) {
        this.autenticacaoService = autenticacaoService;
    }

    @PostMapping("/login")
    public Usuario login(@RequestBody LoginRequest request) {
        return autenticacaoService.autenticar(request.getLogin(), request.getSenha());
    }
}
