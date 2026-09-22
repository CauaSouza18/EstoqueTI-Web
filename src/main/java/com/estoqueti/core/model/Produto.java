package com.estoqueti.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Representa um Produto do estoque.
 *
 * Reaproveitada de data.Produto (Etapa 5) e da versao pura de model.Produto
 * (Etapa 6), mantendo os metodos de regra propria do objeto
 * (calcularLucro/calcularMargemLucro), que fazem sentido continuar aqui pois
 * dependem apenas do proprio estado do Produto - nao de banco de dados nem
 * de outras entidades.
 *
 * Ajuste da Etapa 9: anotada como entidade JPA. id_categoria permanece uma
 * coluna simples (int), sem @ManyToOne, replicando fielmente o esquema
 * original em vez de introduzir uma associacao bidirecional nesta etapa.
 */
@Entity
@Table(name = "produtos")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produto")
    private int idProduto;

    @Column(name = "nome_produto", nullable = false, length = 150)
    private String nomeProduto;

    @Column(name = "descricao", length = 500)
    private String descricao;

    @Column(name = "unidade_medida", length = 10)
    private String unidadeMedida;

    @Column(name = "id_categoria", nullable = false)
    private int idCategoria;

    @Column(name = "marca", length = 100)
    private String marca;

    @Column(name = "preco_custo", precision = 12, scale = 2)
    private BigDecimal precoCusto;

    @Column(name = "preco_venda", precision = 12, scale = 2)
    private BigDecimal precoVenda;

    @Column(name = "quantidade", nullable = false)
    private int quantidade;

    @Column(name = "status", length = 20)
    private String status;

    public Produto() {
        this.quantidade = 0;
        this.status = "Ativo";
        this.precoCusto = BigDecimal.ZERO;
        this.precoVenda = BigDecimal.ZERO;
    }

    public Produto(int idProduto, String nomeProduto, String descricao,
                   String unidadeMedida, int idCategoria, String marca,
                   BigDecimal precoCusto, BigDecimal precoVenda, int quantidade, String status) {
        this.idProduto = idProduto;
        this.nomeProduto = nomeProduto;
        this.descricao = descricao;
        this.unidadeMedida = unidadeMedida;
        this.idCategoria = idCategoria;
        this.marca = marca;
        this.precoCusto = precoCusto;
        this.precoVenda = precoVenda;
        this.quantidade = quantidade;
        this.status = status;
    }

    public int getIdProduto() { return idProduto; }
    public void setIdProduto(int idProduto) { this.idProduto = idProduto; }

    public String getNomeProduto() { return nomeProduto; }
    public void setNomeProduto(String nomeProduto) { this.nomeProduto = nomeProduto; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getUnidadeMedida() { return unidadeMedida; }
    public void setUnidadeMedida(String unidadeMedida) { this.unidadeMedida = unidadeMedida; }

    public int getIdCategoria() { return idCategoria; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public BigDecimal getPrecoCusto() { return precoCusto; }
    public void setPrecoCusto(BigDecimal precoCusto) { this.precoCusto = precoCusto; }

    public BigDecimal getPrecoVenda() { return precoVenda; }
    public void setPrecoVenda(BigDecimal precoVenda) { this.precoVenda = precoVenda; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    /** Lucro unitario (Venda - Custo). */
    public BigDecimal calcularLucro() {
        if (this.precoVenda == null || this.precoCusto == null) {
            return BigDecimal.ZERO;
        }
        return this.precoVenda.subtract(this.precoCusto);
    }

    /** Margem de lucro percentual sobre o custo. */
    public BigDecimal calcularMargemLucro() {
        if (this.precoCusto == null || this.precoCusto.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal lucro = calcularLucro();
        return lucro.divide(this.precoCusto, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .setScale(2, RoundingMode.HALF_UP);
    }
}
