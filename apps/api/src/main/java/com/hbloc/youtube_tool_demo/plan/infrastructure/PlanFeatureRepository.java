package com.hbloc.youtube_tool_demo.plan.infrastructure;

import com.hbloc.youtube_tool_demo.plan.domain.PlanFeature;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanFeatureRepository extends JpaRepository<PlanFeature, Integer> {
}
