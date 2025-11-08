package com.hbloc.youtube_tool_demo.feature.application;

import com.hbloc.youtube_tool_demo.feature.api.modal.PricingRuleDto;

import java.math.BigDecimal;

public interface IPricingRuleService {
    PricingRuleDto getActivePricingRule(String featureCode);
    BigDecimal calculateCost(String featureCode, Long usedUnit);
}
