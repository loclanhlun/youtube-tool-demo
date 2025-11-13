package com.hbloc.youtube_tool_demo.plan.application;

import com.hbloc.youtube_tool_demo.plan.api.mapper.PlanMapper;
import com.hbloc.youtube_tool_demo.plan.api.modal.Feature;
import com.hbloc.youtube_tool_demo.plan.api.modal.Plan;
import com.hbloc.youtube_tool_demo.plan.infrastructure.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanService implements IPlanService {

    private final PlanRepository planRepository;
    private final PlanMapper planMapper;

    @Override
    public List<Plan> getPlans() {
        List<com.hbloc.youtube_tool_demo.plan.domain.Plan> plans
                = planRepository.findAllByActiveIsTrue()
                .orElseThrow(() -> new RuntimeException("No plans found"));
        List<Plan> planList = planMapper.toApiList(plans);
        return planList;
    }
}
