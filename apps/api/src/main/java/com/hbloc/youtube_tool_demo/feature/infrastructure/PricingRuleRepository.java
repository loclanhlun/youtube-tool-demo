package com.hbloc.youtube_tool_demo.feature.infrastructure;

import com.hbloc.youtube_tool_demo.feature.domain.PricingRuleEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PricingRuleRepository extends JpaRepository<PricingRuleEntity, Integer> {

    @Query("SELECT p FROM PricingRuleEntity p " +
            "WHERE p.feature.code = :featureCode " +
                "AND p.effectiveFrom <= CURRENT_TIMESTAMP " +
                "AND (p.effectiveTo IS NULL " +
                "OR p.effectiveTo >= CURRENT_TIMESTAMP) " +
            "ORDER BY p.effectiveFrom DESC"
    )
    @EntityGraph(attributePaths = "feature")
    Optional<PricingRuleEntity> findActiveByFeatureCode(@Param("featureCode") String featureCode);
}
