package com.hbloc.youtube_tool_demo.subscription.api.modal;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class Subscription {
    private String planName;
    private boolean isAutoRenew;
    private Instant startAt;
    private Instant endAt;
    private Instant canceledAt;
    private String cancelReason;
    private String status;
}
