package com.hbloc.youtube_tool_demo.subscription.domain;

import com.hbloc.youtube_tool_demo.common.persistence.BaseEntity;
import com.hbloc.youtube_tool_demo.plan.domain.PlanEntity;
import com.hbloc.youtube_tool_demo.user.domain.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
public class Subscription extends BaseEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "plan_id", nullable = false)
    private Long planId;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "auto_renew", nullable = false)
    private boolean isAutoRenew;

    @Column(name = "start_at", nullable = false)
    private Instant startAt;

    @Column(name = "end_at")
    private Instant endAt;

    @Column(name = "canceled_at")
    private Instant canceledAt;

    @Column(name = "cancel_reason")
    private String cancelReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false, insertable = false)
    private UserEntity users;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "plan_id", referencedColumnName = "id")
    private PlanEntity plan;

    @OneToMany(mappedBy = "subscription")
    private List<SubscriptionFeatureUsage> subscriptionFeatureUsages;

}
