package com.hbloc.youtube_tool_demo.subscription.application;

import com.hbloc.youtube_tool_demo.subscription.api.modal.CreateSubscriptionRequest;
import com.hbloc.youtube_tool_demo.subscription.api.modal.Subscription;

public interface ISubscriptionService {
    Subscription activateSubscription(CreateSubscriptionRequest createSubscriptionRequest);
    void cancelSubscription(String subscriptionId);
    void expireSubscription(String subscriptionId);
}
