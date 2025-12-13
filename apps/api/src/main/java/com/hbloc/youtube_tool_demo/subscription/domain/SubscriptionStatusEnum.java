package com.hbloc.youtube_tool_demo.subscription.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SubscriptionStatusEnum {
    TRIALING("TRIALING"),
    ACTIVE("ACTIVE"),
    EXPIRED("EXPIRED"),
    CANCELED("CANCELED"),
    PENDING("CANCELED");

    private final String value;
}
