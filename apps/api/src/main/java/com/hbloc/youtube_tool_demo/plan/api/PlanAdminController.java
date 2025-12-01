package com.hbloc.youtube_tool_demo.plan.api;

import com.hbloc.youtube_tool_demo.common.modal.util.ResponseUtil;
import com.hbloc.youtube_tool_demo.plan.api.modal.Plan;
import com.hbloc.youtube_tool_demo.plan.api.modal.request.CreatePlanRequest;
import com.hbloc.youtube_tool_demo.plan.api.modal.request.UpdatePlanRequest;
import com.hbloc.youtube_tool_demo.plan.application.IPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/plan")
@PreAuthorize(value = "hasAnyRole('ADMIN')")
@RequiredArgsConstructor
public class PlanAdminController {

    private final IPlanService planService;

    @GetMapping
    public ResponseEntity<?> getPlans() {
        List<Plan> plans = planService.getPlans();
        return ResponseEntity.ok(ResponseUtil.success(plans));
    }

    @PostMapping
    public ResponseEntity<?> addPlan(@Valid @RequestBody CreatePlanRequest request) {
        planService.addPlan(request);
        return ResponseEntity.ok(ResponseUtil.success(null));
    }

    @PutMapping("/{planCode}")
    public ResponseEntity<?> updatePlan(@PathVariable String planCode,
                                        @Valid @RequestBody UpdatePlanRequest request) {
        planService.updatePlan(planCode, request);
        return ResponseEntity.ok(ResponseUtil.success(null));
    }

    @DeleteMapping("/{planCode}")
    public ResponseEntity<?> deletePlan(@PathVariable String planCode) {
        planService.deletePlan(planCode);
        return ResponseEntity.ok(ResponseUtil.success(null));
    }

}
