package com.hbloc.youtube_tool_demo.subscription.application;

import com.hbloc.youtube_tool_demo.common.modal.AppException;
import com.hbloc.youtube_tool_demo.plan.domain.PlanEntity;
import com.hbloc.youtube_tool_demo.plan.infrastructure.PlanRepository;
import com.hbloc.youtube_tool_demo.subscription.api.mapper.SubscriptionMapper;
import com.hbloc.youtube_tool_demo.subscription.api.modal.CreateSubscriptionRequest;
import com.hbloc.youtube_tool_demo.subscription.api.modal.Subscription;
import com.hbloc.youtube_tool_demo.subscription.domain.SubscriptionEntity;
import com.hbloc.youtube_tool_demo.subscription.domain.SubscriptionStatusEnum;
import com.hbloc.youtube_tool_demo.subscription.infrastructure.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;

import static com.hbloc.youtube_tool_demo.common.constant.ResultCode.NOT_FOUND_ERROR;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements ISubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final PlanRepository planRepository;
    private final SubscriptionMapper subscriptionMapper;

    @Override
    public Subscription activateSubscription(CreateSubscriptionRequest request) {
        PlanEntity activePlan = planRepository.findByCode(request.getPlanCode())
                .orElseThrow(() -> new AppException(NOT_FOUND_ERROR.getCode(), "Plan Code not found"));

        if (!activePlan.getActive()) {
            throw new AppException(NOT_FOUND_ERROR.getCode(), "Plan is not active");
        }

        if (!EnumUtils.isValidEnum(SubscriptionStatusEnum.class, request.getStatus())) {
            throw new AppException(NOT_FOUND_ERROR.getCode(), "Subscription status is not valid");
        }

        Instant now = Instant.now();
        SubscriptionEntity savedSubscription = subscriptionRepository.save(
                subscriptionMapper.toSubscriptionEntity(request, activePlan, now)
        );

        return subscriptionMapper.toSubscription(savedSubscription, activePlan);
    }

    @Override
    public void cancelSubscription(String subscriptionId) {

    }

    @Override
    public void expireSubscription(String subscriptionId) {

    }
}
