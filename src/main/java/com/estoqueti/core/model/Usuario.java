package com.estoqueti.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Representa um Usuario do sistema e seu nivel de acesso.
 * Reaproveitada de data.Usuario do projeto desktop.
 *
 * Ajuste da Etapa 9:
 *  - anotada como entidade JPA (ver nota em Categoria.java);
 *  - o campo senha recebe @JsonProperty(access = WRITE_ONLY): a API REST
 *    aceita a senha na entrada (cadastro/login) mas NUNCA a devolve nas
 *    respostas JSON (ex.: GET /api/usuarios), evitando expor senhas em
 *    texto puro para o front-end - um cuidado que nao existia no projeto
 *    desktop original.
 */
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private int idUsuario;

    @Column(name = "nome_usuario", nullable = false, length = 120)
    private String nomeUsuario;

    @Column(name = "login", nullable = false, length = 60, unique = true)
    private String login;

    @Column(name = "senha", nullable = false, length = 100)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String senha;

    @Column(name = "nivel_acesso", length = 20)
    private String nivelAcesso;

    public Usuario() {
        this.nivelAcesso = "Consulta";
    }

    public Usuario(int idUsuario, String nomeUsuario, String login,
                   String senha, String nivelAcesso) {
        this.idUsuario = idUsuario;
        this.nomeUsuario = nomeUsuario;
        this.login = login;
        this.senha = senha;
        this.nivelAcesso = nivelAcesso;
    }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNomeUsuario() { return nomeUsuario; }
    public void setNomeUsuario(String nomeUsuario) { this.nomeUsuario = nomeUsuario; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getNivelAcesso() { return nivelAcesso; }
    public void setNivelAcesso(String nivelAcesso) { this.nivelAcesso = nivelAcesso; }

    public boolean isAdministrador() {
        return "Administrador".equals(this.nivelAcesso);
    }

    public boolean isOperador() {
        return "Operador".equals(this.nivelAcesso);
    }

    public boolean podeOperar() {
        return isAdministrador() || isOperador();
    }
}
