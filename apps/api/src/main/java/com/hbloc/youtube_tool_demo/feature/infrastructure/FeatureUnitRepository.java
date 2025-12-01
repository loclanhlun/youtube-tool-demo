package com.hbloc.youtube_tool_demo.feature.infrastructure;

import com.hbloc.youtube_tool_demo.feature.domain.FeatureUnitEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FeatureUnitRepository extends JpaRepository<FeatureUnitEntity, Integer> {
    Optional<FeatureUnitEntity> findByCode(String code);
}
