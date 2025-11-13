package com.hbloc.youtube_tool_demo.plan.api.modal;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import net.minidev.json.annotate.JsonIgnore;

@Data
public class Feature {

    @JsonIgnore
    @JsonProperty("featureCode")
    private String featureCode;

    @JsonIgnore
    @JsonProperty("includedUnits")
    private Integer includedUnits;
}
