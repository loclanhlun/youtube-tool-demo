package com.hbloc.youtube_tool_demo.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PlanBillingPeriodEnum {
    MONTHLY("MONTHLY", 30),
    YEARLY("YEARLY", 365);

    private final String period;
    private final Integer dayToPlus;
}
