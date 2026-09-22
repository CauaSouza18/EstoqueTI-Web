package com.estoqueti.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa uma Movimentacao de Estoque (Entrada ou Saida).
 * Reaproveitada de data.MovimentacaoEstoque. Anotada como entidade JPA
 * na Etapa 9 (ver nota em Categoria.java).
 */
@Entity
@Table(name = "movimentacoes_estoque")
public class MovimentacaoEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimentacao")
    private int idMovimentacao;

    @Column(name = "id_produto", nullable = false)
    private int idProduto;

    @Column(name = "data_movimentacao", nullable = false)
    private LocalDateTime dataMovimentacao;

    @Column(name = "tipo_movimentacao", nullable = false, length = 10)
    private String tipoMovimentacao; // "Entrada" ou "Saida"

    @Column(name = "quantidade", nullable = false)
    private int quantidade;

    @Column(name = "observacao", length = 300)
    private String observacao;

    @Column(name = "id_usuario", nullable = false)
    private int idUsuario;

    public MovimentacaoEstoque() {
        this.dataMovimentacao = LocalDateTime.now();
    }

    public MovimentacaoEstoque(int idMovimentacao, int idProduto,
                                LocalDateTime dataMovimentacao, String tipoMovimentacao,
                                int quantidade, String observacao, int idUsuario) {
        this.idMovimentacao = idMovimentacao;
        this.idProduto = idProduto;
        this.dataMovimentacao = dataMovimentacao;
        this.tipoMovimentacao = tipoMovimentacao;
        this.quantidade = quantidade;
        this.observacao = observacao;
        this.idUsuario = idUsuario;
    }

    public int getIdMovimentacao() { return idMovimentacao; }
    public void setIdMovimentacao(int idMovimentacao) { this.idMovimentacao = idMovimentacao; }

    public int getIdProduto() { return idProduto; }
    public void setIdProduto(int idProduto) { this.idProduto = idProduto; }

    public LocalDateTime getDataMovimentacao() { return dataMovimentacao; }
    public void setDataMovimentacao(LocalDateTime dataMovimentacao) { this.dataMovimentacao = dataMovimentacao; }

    public String getTipoMovimentacao() { return tipoMovimentacao; }
    public void setTipoMovimentacao(String tipoMovimentacao) { this.tipoMovimentacao = tipoMovimentacao; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public boolean isEntrada() { return "Entrada".equals(this.tipoMovimentacao); }
    public boolean isSaida() { return "Saida".equals(this.tipoMovimentacao); }

    public String getDataMovimentacaoFormatada() {
        return this.dataMovimentacao.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }
}
