package com.hbloc.youtube_tool_demo.plan.api;

import com.hbloc.youtube_tool_demo.plan.api.modal.Plan;
import com.hbloc.youtube_tool_demo.plan.application.IPlanService;
import com.hbloc.youtube_tool_demo.plan.application.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/plan")
@RequiredArgsConstructor
public class PlanController {

    private final IPlanService planService;
    @GetMapping
    public ResponseEntity<?> getPlans () {
        List<Plan> plans = planService.getPlans();
        return ResponseEntity.ok(plans);
    }

}
