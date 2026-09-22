package com.estoqueti.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Representa um Fornecedor. Reaproveitada de data.Fornecedor.
 * Ajuste da Etapa 9: anotada como entidade JPA (ver nota em Categoria.java).
 */
@Entity
@Table(name = "fornecedores")
public class Fornecedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_fornecedor")
    private int idFornecedor;

    @Column(name = "nome_fornecedor", nullable = false, length = 150)
    private String nomeFornecedor;

    @Column(name = "endereco", length = 200)
    private String endereco;

    @Column(name = "telefone", length = 20)
    private String telefone;

    @Column(name = "email", length = 120)
    private String email;

    @Column(name = "cnpj", length = 18, unique = true)
    private String cnpj;

    @Column(name = "status", length = 20)
    private String status;

    public Fornecedor() {
        this.status = "Ativo";
    }

    public Fornecedor(int idFornecedor, String nomeFornecedor, String endereco,
                       String telefone, String email, String cnpj, String status) {
        this.idFornecedor = idFornecedor;
        this.nomeFornecedor = nomeFornecedor;
        this.endereco = endereco;
        this.telefone = telefone;
        this.email = email;
        this.cnpj = cnpj;
        this.status = status;
    }

    public int getIdFornecedor() { return idFornecedor; }
    public void setIdFornecedor(int idFornecedor) { this.idFornecedor = idFornecedor; }

    public String getNomeFornecedor() { return nomeFornecedor; }
    public void setNomeFornecedor(String nomeFornecedor) { this.nomeFornecedor = nomeFornecedor; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
