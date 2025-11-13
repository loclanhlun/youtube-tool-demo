package com.hbloc.youtube_tool_demo.feature.domain;

import com.hbloc.youtube_tool_demo.common.persistence.BaseEntity;
import com.hbloc.youtube_tool_demo.plan.domain.PlanFeature;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "features")
@Getter
@Setter
public class Feature extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(name = "feature_unit_id")
    private Integer featureUnitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feature_unit_id", insertable = false, updatable = false)
    private FeatureUnit featureUnit;

    @OneToMany(mappedBy = "feature")
    private List<PricingRule> pricingRules;

    @OneToMany(mappedBy = "feature")
    private List<PlanFeature> planFeatures;
}
