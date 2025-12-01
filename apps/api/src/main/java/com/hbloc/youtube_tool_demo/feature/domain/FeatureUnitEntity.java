package com.hbloc.youtube_tool_demo.feature.domain;

import com.hbloc.youtube_tool_demo.common.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import com.hbloc.youtube_tool_demo.feature.domain.FeatureEntity;

@Entity
@Table(name = "feature_units")
@Getter
@Setter
public class FeatureUnitEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "feature_units_id_seq", sequenceName = "feature_units_id_seq")
    private Integer id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "featureUnit")
    private List<FeatureEntity> features;
}
