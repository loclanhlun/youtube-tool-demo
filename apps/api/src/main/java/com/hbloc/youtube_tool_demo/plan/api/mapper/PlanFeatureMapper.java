package com.hbloc.youtube_tool_demo.plan.api.mapper;

import com.hbloc.youtube_tool_demo.plan.api.modal.request.CreatePlanFeatureRequest;
import com.hbloc.youtube_tool_demo.plan.domain.OveragePolicyEnum;
import com.hbloc.youtube_tool_demo.plan.domain.PlanFeatureEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlanFeatureMapper {

    default PlanFeatureEntity toPlanFeature(CreatePlanFeatureRequest featureRequest, Integer planId, Integer featureId) {
        PlanFeatureEntity planFeature = new PlanFeatureEntity();
        planFeature.setPlanId(planId);
        planFeature.setFeatureId(featureId);
        planFeature.setOveragePolicy(OveragePolicyEnum.CREDITS);
        planFeature.setIncludedUnits(featureRequest.getIncludedUnit());
        return planFeature;
    }
}
