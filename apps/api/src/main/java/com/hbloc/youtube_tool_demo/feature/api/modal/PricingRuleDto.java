package com.hbloc.youtube_tool_demo.feature.api.modal;

import lombok.Data;


import java.math.BigDecimal;
import java.time.Instant;

@Data
public class PricingRuleDto {
    private String featureName;
    private BigDecimal unitCostInCredits;
    private String unit;
    private Integer minStep;
    private String notes;
    private Instant effectiveFrom;
    private Instant effectiveTo;
}
