package com.hbloc.youtube_tool_demo.plan.api.modal.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePlanFeatureRequest {

    @NotBlank(message = "Plan code is mandatory")
    private String planCode;

    @NotBlank(message = "Feature code is mandatory")
    private String featureCode;

    @NotNull(message = "Included unit is mandatory")
    @PositiveOrZero(message = "IncludedUnit must be start from 0")
    private Long includedUnit;

}
