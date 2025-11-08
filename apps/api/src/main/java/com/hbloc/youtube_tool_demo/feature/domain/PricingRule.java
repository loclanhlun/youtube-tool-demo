package com.hbloc.youtube_tool_demo.feature.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "pricing_rules")
@Getter
@Setter
public class PricingRule {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "pricing_rules_id_seq", sequenceName = "pricing_rules_id_seq")
    private Integer id;

    @Column(name = "feature_id", nullable = false)
    private Integer featureId;

    @Column(name = "unit_cost_in_credits", precision = 12, scale = 4, nullable = false)
    private BigDecimal unitCostInCredits;

    @Column(name = "unit", nullable = false)
    private String unit;

    @Column(name = "min_step", nullable = false)
    private Integer minStep;

    @Column(name = "notes")
    private String notes;

    @Column(name = "effective_from", nullable = false)
    private Instant effectiveFrom;

    @Column(name = "effective_to")
    private Instant effectiveTo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "feature_id", nullable = false, updatable = false, insertable = false)
    private Feature feature;

}
