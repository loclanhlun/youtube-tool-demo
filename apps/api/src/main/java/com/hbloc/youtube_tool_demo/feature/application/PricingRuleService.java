package com.hbloc.youtube_tool_demo.feature.application;

import com.hbloc.youtube_tool_demo.feature.api.modal.PricingRuleDto;
import com.hbloc.youtube_tool_demo.feature.domain.PricingRule;
import com.hbloc.youtube_tool_demo.feature.infrastructure.PricingRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PricingRuleService implements IPricingRuleService {

    private final PricingRuleRepository pricingRuleRepository;

    @Override
    public PricingRuleDto getActivePricingRule(String featureCode) {
        PricingRule pricingRule = pricingRuleRepository.findActiveByFeatureCode(featureCode)
                .orElseThrow(() -> new RuntimeException("No active price rule found for featureCode: " + featureCode));

        PricingRuleDto pricingRuleDto = new PricingRuleDto();
        pricingRuleDto.setFeatureName(pricingRule.getFeature().getName());
        pricingRuleDto.setUnitCostInCredits(pricingRule.getUnitCostInCredits());
        pricingRuleDto.setMinStep(pricingRule.getMinStep());
        pricingRuleDto.setNotes(pricingRule.getNotes());
        pricingRuleDto.setEffectiveFrom(pricingRule.getEffectiveFrom());
        pricingRuleDto.setEffectiveTo(pricingRule.getEffectiveTo());
        pricingRuleDto.setUnit(pricingRule.getUnit());

        return pricingRuleDto;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateCost(String featureCode, Long usedUnit) {
        PricingRule pricingRule = pricingRuleRepository.findActiveByFeatureCode(featureCode)
                .orElseThrow(() -> new RuntimeException("No active price rule found for featureCode: " + featureCode));

        int minStep = pricingRule.getMinStep();
        BigDecimal unitCost = pricingRule.getUnitCostInCredits();

        long steps = (long) Math.ceil((double) usedUnit / minStep);
        return unitCost.multiply(BigDecimal.valueOf(steps * minStep));
    }
}
