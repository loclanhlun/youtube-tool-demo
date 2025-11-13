package com.hbloc.youtube_tool_demo.plan.domain;

import com.hbloc.youtube_tool_demo.common.persistence.BaseEntity;
import com.hbloc.youtube_tool_demo.feature.domain.Feature;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "plan_features")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class PlanFeature extends BaseEntity {

    @Column(name = "plan_id", nullable = false)
    private Integer planId;

    @Column(name = "feature_id", nullable = false)
    private Integer featureId;

    @Column(name = "included_units", nullable = false)
    private Long includedUnits;

    @Enumerated(EnumType.STRING)
    @Column(name = "overage_policy", nullable = false)
    private OveragePolicyEnum overagePolicy;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feature_id", nullable = false, updatable = false, insertable = false)
    private Feature feature;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false, updatable = false, insertable = false)
    private Plan plan;

}
