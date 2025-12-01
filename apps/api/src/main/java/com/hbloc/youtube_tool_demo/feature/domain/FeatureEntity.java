package com.hbloc.youtube_tool_demo.feature.domain;

import com.hbloc.youtube_tool_demo.common.persistence.BaseEntity;
import com.hbloc.youtube_tool_demo.plan.domain.PlanFeatureEntity;
import com.hbloc.youtube_tool_demo.feature.domain.FeatureUnitEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "features")
@Getter
@Setter
public class FeatureEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "feature_id_seq", sequenceName = "feature_id_seq")
    private Integer id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(name = "feature_unit_id")
    private Integer featureUnitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feature_unit_id", insertable = false, updatable = false)
    private FeatureUnitEntity featureUnit;

    @OneToMany(mappedBy = "feature")
    private List<PricingRuleEntity> pricingRules;

    @OneToMany(mappedBy = "feature")
    private List<PlanFeatureEntity> planFeatures;
}
