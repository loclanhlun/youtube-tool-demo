package com.hbloc.youtube_tool_demo.plan.infrastructure;

import com.hbloc.youtube_tool_demo.plan.domain.Plan;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlanRepository extends JpaRepository<Plan, Integer> {

    @EntityGraph(attributePaths = {"planFeatures", "planFeatures.feature"})
    Optional<List<Plan>> findAllByActiveIsTrue();
}
