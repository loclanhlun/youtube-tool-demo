package com.hbloc.youtube_tool_demo.feature.infrastructure;

import com.hbloc.youtube_tool_demo.feature.domain.FeatureUnit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FeatureUnitRepository extends JpaRepository<FeatureUnit, Integer> {
    Optional<FeatureUnit> findByCode(String code);
}
