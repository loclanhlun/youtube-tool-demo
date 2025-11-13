package com.hbloc.youtube_tool_demo.plan.api.mapper;

import com.hbloc.youtube_tool_demo.plan.api.modal.Feature;
import com.hbloc.youtube_tool_demo.plan.api.modal.Plan;
import com.hbloc.youtube_tool_demo.plan.domain.PlanFeature;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PlanMapper {

    @Mapping(target = "features", source = "planFeatures")
    Plan toApi(com.hbloc.youtube_tool_demo.plan.domain.Plan plan);

    List<Plan> toApiList(List<com.hbloc.youtube_tool_demo.plan.domain.Plan> plan);

    @Mapping(target = "featureCode", source = "feature.code")
    Feature mapPlanFeatureToFeature(PlanFeature planFeature);

    List<Feature> mapPlanFeatureListToFeatureList(List<PlanFeature> planFeatureList);

}
