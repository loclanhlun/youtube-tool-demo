package com.hbloc.youtube_tool_demo.feature.infrastructure;

import com.hbloc.youtube_tool_demo.feature.domain.Feature;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FeatureRepository extends JpaRepository<Feature, Integer> {
    @EntityGraph(attributePaths = "featureUnit")
    List<Feature> findAll();
    Optional<Feature> findByCode(String code);
}
