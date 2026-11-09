package br.com.lojinhadakatia.offering.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "candle_options", uniqueConstraints = @UniqueConstraint(
    name = "uq_candle_option", columnNames = {"offering_id", "kind", "option_value"}))
public class CandleOption {
    public enum Kind { AROMA, COLOR, PACKAGING }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "offering_id", nullable = false) private ServiceOffering offering;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private Kind kind;
    @Column(name = "option_value", nullable = false, length = 100) private String value;
    @Column(nullable = false) private boolean active;

    protected CandleOption() {}
    public CandleOption(ServiceOffering offering, Kind kind, String value, boolean active) {
        this.offering = offering; this.kind = kind; this.value = value; this.active = active;
    }
    public Long getId() {return id;}
    public Kind getKind() {return kind;}
    public String getValue() {return value;}
    public boolean isActive() {return active;}
    public void setActive(boolean active) {this.active = active;}
}
