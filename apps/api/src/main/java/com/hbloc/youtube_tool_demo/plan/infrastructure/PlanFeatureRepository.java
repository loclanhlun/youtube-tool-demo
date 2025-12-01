package com.hbloc.youtube_tool_demo.plan.infrastructure;

import com.hbloc.youtube_tool_demo.plan.domain.PlanFeatureEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlanFeatureRepository extends JpaRepository<PlanFeatureEntity, Integer> {
    Optional<List<PlanFeatureEntity>> findByPlanId(Integer planId);
}
