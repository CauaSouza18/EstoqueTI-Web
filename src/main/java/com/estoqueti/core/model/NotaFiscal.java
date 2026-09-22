package com.estoqueti.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Representa uma Nota Fiscal. Reaproveitada de data.NotaFiscal.
 * Anotada como entidade JPA na Etapa 9 (ver nota em Categoria.java).
 */
@Entity
@Table(name = "notas_fiscais")
public class NotaFiscal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_nf")
    private int idNf;

    @Column(name = "numero_nf", nullable = false, length = 30, unique = true)
    private String numeroNf;

    @Column(name = "data_emissao", nullable = false)
    private LocalDate dataEmissao;

    @Column(name = "valor_total", precision = 12, scale = 2)
    private BigDecimal valorTotal;

    @Column(name = "id_fornecedor", nullable = false)
    private int idFornecedor;

    public NotaFiscal() {
        this.dataEmissao = LocalDate.now();
    }

    public NotaFiscal(int idNf, String numeroNf, LocalDate dataEmissao,
                       BigDecimal valorTotal, int idFornecedor) {
        this.idNf = idNf;
        this.numeroNf = numeroNf;
        this.dataEmissao = dataEmissao;
        this.valorTotal = valorTotal;
        this.idFornecedor = idFornecedor;
    }

    public int getIdNf() { return idNf; }
    public void setIdNf(int idNf) { this.idNf = idNf; }

    public String getNumeroNf() { return numeroNf; }
    public void setNumeroNf(String numeroNf) { this.numeroNf = numeroNf; }

    public LocalDate getDataEmissao() { return dataEmissao; }
    public void setDataEmissao(LocalDate dataEmissao) { this.dataEmissao = dataEmissao; }

    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }

    public int getIdFornecedor() { return idFornecedor; }
    public void setIdFornecedor(int idFornecedor) { this.idFornecedor = idFornecedor; }

    public String getDataEmissaoFormatada() {
        return this.dataEmissao.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}
