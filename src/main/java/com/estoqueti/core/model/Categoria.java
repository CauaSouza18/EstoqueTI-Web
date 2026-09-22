package com.estoqueti.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Representa uma Categoria de produto. Reaproveitada de data.Categoria
 * (Etapa 5) e da versao pura de model.Categoria (Etapa 6).
 *
 * Ajuste da Etapa 9: a classe passa a acumular tambem o papel de entidade
 * JPA (anotacoes @Entity/@Id/@Column), opcao pragmatica para nao duplicar
 * classes de modelo + entidade neste projeto academico. Os metodos e o
 * estado permanecem exatamente os mesmos da Etapa 6.
 */
@Entity
@Table(name = "categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private int idCategoria;

    @Column(name = "nome_categoria", nullable = false, length = 100)
    private String nomeCategoria;

    public Categoria() { }

    public Categoria(int idCategoria, String nomeCategoria) {
        this.idCategoria = idCategoria;
        this.nomeCategoria = nomeCategoria;
    }

    public int getIdCategoria() { return idCategoria; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }

    public String getNomeCategoria() { return nomeCategoria; }
    public void setNomeCategoria(String nomeCategoria) { this.nomeCategoria = nomeCategoria; }
}
