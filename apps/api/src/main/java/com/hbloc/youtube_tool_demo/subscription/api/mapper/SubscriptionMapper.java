package com.hbloc.youtube_tool_demo.subscription.api.mapper;

import com.hbloc.youtube_tool_demo.plan.domain.PlanEntity;
import com.hbloc.youtube_tool_demo.subscription.api.modal.CreateSubscriptionRequest;
import com.hbloc.youtube_tool_demo.subscription.api.modal.Subscription;
import com.hbloc.youtube_tool_demo.subscription.domain.SubscriptionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.time.Instant;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

    @Mappings({
            @Mapping(target = "planId", source = "activePlan.id"),
            @Mapping(target = "status", expression = "java(com.hbloc.youtube_tool_demo.subscription.domain.SubscriptionStatusEnum.valueOf(request.getStatus()))"),
            @Mapping(target = "startAt", source = "now"),
            @Mapping(
                    target = "endAt",
                    expression = "java(now.plus(com.hbloc.youtube_tool_demo.common.constant.PlanBillingPeriodEnum.valueOf(String.valueOf(activePlan.getBillingPeriod())).getDayToPlus(), java.time.temporal.ChronoUnit.DAYS))"
            ),
            @Mapping(target = "id", source = "request.userId"),
            @Mapping(target = "autoRenew", source = "request.autoRenew"),
            @Mapping(target = "userId", source = "request.userId"),
            @Mapping(target = "canceledAt", ignore = true),
            @Mapping(target = "cancelReason", ignore = true),
            @Mapping(target = "user", ignore = true),
            @Mapping(target = "plan", ignore = true),
            @Mapping(target = "subscriptionFeatureUsageEntities", ignore = true)
    })
    SubscriptionEntity toSubscriptionEntity(CreateSubscriptionRequest request, PlanEntity activePlan, Instant now);

    @Mappings({
            @Mapping(target = "planName", source = "plan.name"),
            @Mapping(target = "autoRenew", source = "subscriptionEntity.autoRenew"),
            @Mapping(target = "startAt", source = "subscriptionEntity.startAt"),
            @Mapping(target = "endAt", source = "subscriptionEntity.endAt"),
            @Mapping(target = "status", expression = "java(subscriptionEntity.getStatus().getValue())"),
            @Mapping(target = "canceledAt", source = "subscriptionEntity.canceledAt"),
            @Mapping(target = "cancelReason", source = "subscriptionEntity.cancelReason")
    })
    Subscription toSubscription(SubscriptionEntity subscriptionEntity, PlanEntity plan);
}
