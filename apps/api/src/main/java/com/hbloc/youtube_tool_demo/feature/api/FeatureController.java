package com.hbloc.youtube_tool_demo.feature.api;

import com.hbloc.youtube_tool_demo.feature.application.IFeatureService;
import com.hbloc.youtube_tool_demo.feature.application.IPricingRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/features")
@RequiredArgsConstructor
public class FeatureController {

    private final IFeatureService featureService;
    private final IPricingRuleService pricingRuleService;

    @GetMapping(value = "/{featureCode}")
    public ResponseEntity<?> getFeatures(@PathVariable String featureCode) {
        return ResponseEntity.ok(pricingRuleService.getActivePricingRule(featureCode));
    }

}
