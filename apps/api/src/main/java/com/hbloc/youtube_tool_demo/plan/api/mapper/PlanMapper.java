package com.hbloc.youtube_tool_demo.plan.api.mapper;

import com.hbloc.youtube_tool_demo.plan.api.modal.Feature;
import com.hbloc.youtube_tool_demo.plan.api.modal.Plan;
import com.hbloc.youtube_tool_demo.plan.api.modal.request.CreatePlanRequest;
import com.hbloc.youtube_tool_demo.plan.api.modal.request.UpdatePlanRequest;
import com.hbloc.youtube_tool_demo.plan.domain.PlanEntity;
import com.hbloc.youtube_tool_demo.plan.domain.PlanFeatureEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PlanMapper {

    @Mapping(target = "features", source = "planFeatures")
    @Mapping(target = "id", ignore = true)
    Plan toApi(PlanEntity plan);

    List<Plan> toApiList(List<PlanEntity> plan);

    @Mapping(target = "featureCode", source = "feature.code")
    Feature mapPlanFeatureToFeature(PlanFeatureEntity planFeature);

    List<Feature> mapPlanFeatureListToFeatureList(List<PlanFeatureEntity> planFeatureList);

    PlanEntity toPlanEntity(CreatePlanRequest plan);

    PlanEntity toPlanEntity(UpdatePlanRequest plan);

}
