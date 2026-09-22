package com.estoqueti.web.dto;

/** Corpo das requisicoes POST/PUT /api/categorias. */
public class CategoriaRequest {

    private String nomeCategoria;

    public CategoriaRequest() { }

    public String getNomeCategoria() { return nomeCategoria; }
    public void setNomeCategoria(String nomeCategoria) { this.nomeCategoria = nomeCategoria; }
}
