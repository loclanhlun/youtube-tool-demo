package com.hbloc.youtube_tool_demo.feature.application;

import com.hbloc.youtube_tool_demo.feature.api.modal.FeatureResponse;
import com.hbloc.youtube_tool_demo.feature.infrastructure.FeatureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeatureService implements IFeatureService {
    private final FeatureRepository featureRepository;

    @Override
    public List<FeatureResponse> findAllFeature() {
        List<FeatureResponse> list = featureRepository.findAll().stream().map(f -> {
            FeatureResponse featureResponse = new FeatureResponse();
            featureResponse.setCode(f.getCode());
            featureResponse.setName(f.getName());
            featureResponse.setFeatureUnitName(f.getFeatureUnit().getName());
            return featureResponse;
        }).toList();
        return list;
    }
}
