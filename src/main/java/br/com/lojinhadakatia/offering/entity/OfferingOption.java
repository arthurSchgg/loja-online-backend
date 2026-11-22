package br.com.lojinhadakatia.offering.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/** Selectable customization for candle offering models. */
@Entity
@Table(name = "offering_options")
public class OfferingOption {
    public enum Kind { AROMA, COLOR, PACKAGING }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "offering_id", nullable = false)
    private ServiceOffering offering;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Kind kind;

    @Column(nullable = false, length = 120)
    private String label;

    @Column(nullable = false)
    private int position;

    @Column(nullable = false)
    private boolean active;

    protected OfferingOption() {}

    public OfferingOption(ServiceOffering offering, Kind kind, String label,
                          int position, boolean active) {
        this.offering = offering;
        this.kind = kind;
        this.label = label;
        this.position = position;
        this.active = active;
    }

    public Long getId() { return id; }
    // Not a JavaBeans property: repository paths such as OfferingId must
    // continue resolving the JPA relationship offering.id.
    public Long offeringId() { return offering.getId(); }
    public Kind getKind() { return kind; }
    public String getLabel() { return label; }
    public int getPosition() { return position; }
    public boolean isActive() { return active; }
}
