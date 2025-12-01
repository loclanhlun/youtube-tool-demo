package com.hbloc.youtube_tool_demo.subscription.domain;

import com.hbloc.youtube_tool_demo.common.persistence.BaseEntity;
import com.hbloc.youtube_tool_demo.feature.domain.FeatureEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "subscription_feature_usage")
@Getter
@Setter
public class SubscriptionFeatureUsage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subscription_feature_usage_id_seq")
    @SequenceGenerator(name = "subscription_feature_usage_id_seq", sequenceName = "subscription_feature_usage_id_seq", allocationSize = 1)
    private Integer id;

    @Column(name = "subscription_id", nullable = false)
    private UUID subscriptionId;

    @Column(name = "feature_id", nullable = false)
    private Integer featureId;

    @Column(name = "period_start", nullable = false)
    private Instant periodStart;

    @Column(name = "period_end")
    private Instant periodEnd;

    @Column(name = "used_units")
    private Long usedUnits;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feature_id", nullable = false, updatable = false, insertable = false)
    private FeatureEntity feature;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false, updatable = false, insertable = false)
    private Subscription subscription;
}
