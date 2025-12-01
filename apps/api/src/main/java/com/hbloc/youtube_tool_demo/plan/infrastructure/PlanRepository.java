package com.hbloc.youtube_tool_demo.plan.infrastructure;

import com.hbloc.youtube_tool_demo.plan.domain.PlanEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PlanRepository extends JpaRepository<PlanEntity, Integer> {

    @EntityGraph(attributePaths = {"planFeatures", "planFeatures.feature"})
    Optional<List<PlanEntity>> findAllByActiveIsTrue();

    Optional<PlanEntity> findByCode(String planCode);

    Optional<PlanEntity> findByCodeNotIn(Collection<String> code);
}
