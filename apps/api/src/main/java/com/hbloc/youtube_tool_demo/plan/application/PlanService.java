package com.hbloc.youtube_tool_demo.plan.application;

import com.hbloc.youtube_tool_demo.common.constant.ResultCode;
import com.hbloc.youtube_tool_demo.common.modal.AppException;
import com.hbloc.youtube_tool_demo.feature.domain.FeatureEntity;
import com.hbloc.youtube_tool_demo.feature.infrastructure.FeatureRepository;
import com.hbloc.youtube_tool_demo.plan.api.mapper.PlanFeatureMapper;
import com.hbloc.youtube_tool_demo.plan.api.mapper.PlanMapper;
import com.hbloc.youtube_tool_demo.plan.api.modal.Plan;
import com.hbloc.youtube_tool_demo.plan.api.modal.request.CreatePlanRequest;
import com.hbloc.youtube_tool_demo.plan.api.modal.request.UpdatePlanFeatureRequest;
import com.hbloc.youtube_tool_demo.plan.api.modal.request.UpdatePlanRequest;
import com.hbloc.youtube_tool_demo.plan.domain.BillingPeriodEnum;
import com.hbloc.youtube_tool_demo.plan.domain.OveragePolicyEnum;
import com.hbloc.youtube_tool_demo.plan.domain.PlanEntity;
import com.hbloc.youtube_tool_demo.plan.domain.PlanFeatureEntity;
import com.hbloc.youtube_tool_demo.plan.infrastructure.PlanFeatureRepository;
import com.hbloc.youtube_tool_demo.plan.infrastructure.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PlanService implements IPlanService {

    private final PlanRepository planRepository;
    private final FeatureRepository featureRepository;
    private final PlanFeatureRepository planFeatureRepository;
    private final PlanFeatureMapper planFeatureMapper;
    private final PlanMapper planMapper;

    @Override
    public List<Plan> getPlans() {
        List<PlanEntity> plans
                = planRepository.findAllByActiveIsTrue()
                .orElseThrow(() -> new RuntimeException("No plans found"));
        return planMapper.toApiList(plans);
    }

    @Override
    @Transactional
    public void updatePlan(String planCode, UpdatePlanRequest updatePlanRequest) {
        PlanEntity plan =
                planRepository
                        .findByCode(planCode)
                        .orElseThrow(() -> new AppException(
                                ResultCode.NOT_FOUND_ERROR.getCode(),
                                ResultCode.NOT_FOUND_ERROR.getMessage())
                        );

        plan.setActive(updatePlanRequest.getIsActive());
        plan.setName(updatePlanRequest.getName());
        plan.setPrice(updatePlanRequest.getPrice());
        plan.setName(updatePlanRequest.getName());
        plan.setCurrency(updatePlanRequest.getCurrency());
        plan.setBillingPeriod(BillingPeriodEnum.valueOf(updatePlanRequest.getBillingPeriod()));


        List<PlanFeatureEntity> planFeaturesToSave = planFeatureRepository
                .findByPlanId(plan.getId()).orElseThrow(
                        () -> new AppException(
                                ResultCode.NOT_FOUND_ERROR.getCode(),
                                ResultCode.NOT_FOUND_ERROR.getMessage()
                        )
                );

        planFeaturesToSave.forEach(feature -> {
            Map<String, UpdatePlanFeatureRequest> updatePlanFeatureRequestMap
                    = updatePlanRequest.getFeatures();
            if (updatePlanFeatureRequestMap.containsKey(feature.getFeature().getCode())) {
                UpdatePlanFeatureRequest updatePlanFeatureRequest = updatePlanFeatureRequestMap.get(feature.getFeature().getCode());
                feature.setOveragePolicy(OveragePolicyEnum.valueOf(updatePlanFeatureRequest.getOveragePolicy()));
                feature.setIncludedUnits(updatePlanFeatureRequest.getIncludedUnit());
            }
        });
        planRepository.save(plan);

        planFeatureRepository.saveAll(planFeaturesToSave);
    }

    @Override
    @Transactional
    public void addPlan(CreatePlanRequest addPlanRequest) {
        if (planRepository.findByCode(addPlanRequest.getCode()).isPresent()) {
            throw new AppException(ResultCode.DUPLICATE_ERROR.getCode(), ResultCode.DUPLICATE_ERROR.getMessage());
        }
        var planToSave = planMapper.toPlanEntity(addPlanRequest);

        var savedPlan = planRepository.save(planToSave);

        //save plans feature
        List<PlanFeatureEntity> planFeaturesToSave = new ArrayList<>();
        addPlanRequest.getFeatures().forEach(featureRequest -> {
            FeatureEntity feature = featureRepository.findByCode(featureRequest.getFeatureCode())
                    .orElseThrow(() -> new AppException(
                            ResultCode.NOT_FOUND_ERROR.getCode(),
                            ResultCode.NOT_FOUND_ERROR.getMessage())
                    );

            planFeaturesToSave.add(
                    planFeatureMapper.toPlanFeature(
                            featureRequest,
                            savedPlan.getId(),
                            feature.getId()
                    )
            );
        });

        planFeatureRepository.saveAll(planFeaturesToSave);
    }

    @Override
    public void deletePlan(String code) {
        PlanEntity plan =
                planRepository
                        .findByCode(code)
                        .orElseThrow(() -> new RuntimeException("Plan not found"));

        plan.setActive(false);

        planRepository.save(plan);
    }
}
