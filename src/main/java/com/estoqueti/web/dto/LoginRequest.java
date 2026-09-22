package com.estoqueti.web.dto;

/** Corpo da requisicao POST /api/auth/login. */
public class LoginRequest {

    private String login;
    private String senha;

    public LoginRequest() { }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}
