package com.hbloc.youtube_tool_demo.subscription.api.modal;


import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateSubscriptionRequest {
    private UUID userId;
    private String planCode;
    private String status;
    private boolean isAutoRenew;
}
