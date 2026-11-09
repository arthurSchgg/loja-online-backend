package br.com.lojinhadakatia.catalog.entity;

import jakarta.persistence.*;
import java.time.Instant;

/** Registro persistente de alteração de estoque; não representa pedido ou venda. */
@Entity
@Table(name = "product_stock_movements")
public class ProductStockMovement {
    public enum Type { ENTRY, EXIT, ADJUSTMENT }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false, length = 16)
    private Type type;
    @Column(nullable = false)
    private int quantity;
    @Column(name = "resulting_quantity", nullable = false)
    private int resultingQuantity;
    @Column(nullable = false, length = 500)
    private String reason;
    @Column(nullable = false, length = 180)
    private String actor;
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected ProductStockMovement() {}

    public ProductStockMovement(Product product, Type type, int quantity, int resultingQuantity,
                                String reason, String actor) {
        this.product = product;
        this.type = type;
        this.quantity = quantity;
        this.resultingQuantity = resultingQuantity;
        this.reason = reason;
        this.actor = actor;
    }

    @PrePersist void created() { if (createdAt == null) createdAt = Instant.now(); }

    public Long getId() { return id; }
    public Type getType() { return type; }
    public int getQuantity() { return quantity; }
    public int getResultingQuantity() { return resultingQuantity; }
    public String getReason() { return reason; }
    public String getActor() { return actor; }
    public Instant getCreatedAt() { return createdAt; }
}
