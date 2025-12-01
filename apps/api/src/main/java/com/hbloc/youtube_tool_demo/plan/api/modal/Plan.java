package com.hbloc.youtube_tool_demo.plan.api.modal;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import net.minidev.json.annotate.JsonIgnore;

import java.math.BigDecimal;
import java.util.List;

@Data
public class Plan {

    @JsonIgnore
    @JsonProperty("id")
    private Integer id;

    @JsonIgnore
    @JsonProperty("code")
    private String code;

    @JsonIgnore
    @JsonProperty("name")
    private String name;

    @JsonIgnore
    @JsonProperty("price")
    private BigDecimal price;

    @JsonIgnore
    @JsonProperty("currency")
    private String currency;

    @JsonIgnore
    @JsonProperty("billingPeriod")
    private String billingPeriod;

    @JsonIgnore
    @JsonProperty("features")
    private List<Feature> features;
}
