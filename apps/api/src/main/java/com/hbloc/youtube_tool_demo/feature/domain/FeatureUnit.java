package com.hbloc.youtube_tool_demo.feature.domain;

import com.hbloc.youtube_tool_demo.common.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "feature_units")
@Getter
@Setter
public class FeatureUnit extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "featureUnit")
    private List<Feature> features;
}
