package com.hbloc.youtube_tool_demo.plan.api.modal.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePlanFeatureRequest {

    @NotNull(message = "Included unit code is mandatory")
    private Long includedUnit;

    @NotBlank(message = "Overage policy code is mandatory")
    private String overagePolicy;
}
