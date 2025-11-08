package com.hbloc.youtube_tool_demo.feature.application;

import com.hbloc.youtube_tool_demo.feature.api.modal.FeatureResponse;

import java.util.List;

public interface IFeatureService {

    List<FeatureResponse> findAllFeature();
}
