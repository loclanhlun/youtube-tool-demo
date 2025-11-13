package com.hbloc.youtube_tool_demo.plan.domain;

import com.hbloc.youtube_tool_demo.common.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "plans")
@Getter
@Setter
public class Plan extends BaseEntity {

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price", precision = 12, scale = 4, nullable = false)
    private BigDecimal price;

    @Column(name = "currency", nullable = false)
    private char currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "billing_period", nullable = false)
    private BillingPeriodEnum billingPeriod;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @OneToMany(mappedBy = "plan")
    private List<PlanFeature> planFeatures;
}
