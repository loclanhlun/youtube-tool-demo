package com.hbloc.youtube_tool_demo.feature.infrastructure;

import com.hbloc.youtube_tool_demo.feature.domain.FeatureEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FeatureRepository extends JpaRepository<FeatureEntity, Integer> {
    @EntityGraph(attributePaths = "featureUnit")
    List<FeatureEntity> findAll();
    Optional<FeatureEntity> findByCode(String code);
    Optional<List<FeatureEntity>> findByCodeIn(List<String> codes);
}
