package com.hbloc.youtube_tool_demo.plan.application;


import com.hbloc.youtube_tool_demo.plan.api.modal.Plan;
import com.hbloc.youtube_tool_demo.plan.api.modal.request.CreatePlanRequest;
import com.hbloc.youtube_tool_demo.plan.api.modal.request.UpdatePlanRequest;

import java.util.List;

public interface IPlanService {
    List<Plan> getPlans();
    void updatePlan(String planCode, UpdatePlanRequest updatePlanRequest);
    void addPlan(CreatePlanRequest addPlanRequest);
    void deletePlan(String code);
}
