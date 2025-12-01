package com.hbloc.youtube_tool_demo.feature.application;

import com.hbloc.youtube_tool_demo.feature.api.modal.PricingRuleDto;
import com.hbloc.youtube_tool_demo.feature.domain.FeatureEntity;
import com.hbloc.youtube_tool_demo.feature.domain.PricingRuleEntity;
import com.hbloc.youtube_tool_demo.feature.infrastructure.PricingRuleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PricingRuleServiceTest {

    @Mock
    private PricingRuleRepository pricingRuleRepository;

    @InjectMocks
    private PricingRuleService pricingRuleService;

    @Test
    void getActivePricingRule_returnsMappedDto() {
        FeatureEntity feature = new FeatureEntity();
        feature.setName("Uploads");

        Instant effectiveFrom = Instant.parse("2024-01-01T00:00:00Z");
        Instant effectiveTo = Instant.parse("2024-12-31T23:59:59Z");

        PricingRuleEntity pricingRule = new PricingRuleEntity();
        pricingRule.setFeature(feature);
        pricingRule.setUnitCostInCredits(new BigDecimal("1.25"));
        pricingRule.setUnit("second");
        pricingRule.setMinStep(5);
        pricingRule.setNotes("note");
        pricingRule.setEffectiveFrom(effectiveFrom);
        pricingRule.setEffectiveTo(effectiveTo);

        when(pricingRuleRepository.findActiveByFeatureCode("UPLOADS")).thenReturn(Optional.of(pricingRule));

        PricingRuleDto pricingRuleDto = pricingRuleService.getActivePricingRule("UPLOADS");

        assertEquals("Uploads", pricingRuleDto.getFeatureName());
        assertEquals(new BigDecimal("1.25"), pricingRuleDto.getUnitCostInCredits());
        assertEquals("second", pricingRuleDto.getUnit());
        assertEquals(5, pricingRuleDto.getMinStep());
        assertEquals("note", pricingRuleDto.getNotes());
        assertEquals(effectiveFrom, pricingRuleDto.getEffectiveFrom());
        assertEquals(effectiveTo, pricingRuleDto.getEffectiveTo());
    }

    @Test
    void getActivePricingRule_throwsWhenMissing() {
        when(pricingRuleRepository.findActiveByFeatureCode("MISSING")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> pricingRuleService.getActivePricingRule("MISSING"));
    }

    @Test
    void calculateCost_roundsUpToMinStep() {
        PricingRuleEntity pricingRule = new PricingRuleEntity();
        pricingRule.setUnitCostInCredits(new BigDecimal("2.50"));
        pricingRule.setMinStep(10);
        pricingRule.setFeature(new FeatureEntity());

        when(pricingRuleRepository.findActiveByFeatureCode("UPLOADS")).thenReturn(Optional.of(pricingRule));

        BigDecimal cost = pricingRuleService.calculateCost("UPLOADS", 15L);

        assertEquals(new BigDecimal("50.00"), cost);
    }

    @Test
    void calculateCost_throwsWhenMissingPricingRule() {
        when(pricingRuleRepository.findActiveByFeatureCode("UPLOADS")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> pricingRuleService.calculateCost("UPLOADS", 10L));
    }
}
