package com.hbloc.youtube_tool_demo.plan.api.modal.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class UpdatePlanRequest {

    @NotBlank(message = "name is mandatory")
    @Length(min = 1, max = 155, message = "Limit characters is 50")
    private String name;

    @Min(value = 0)
    @DecimalMin(value = "0.01", message = "price must be greater than 0.01")
    private BigDecimal price;

    @NotBlank(message = "currency is mandatory")
    @Pattern(regexp = "^(USD|VND)$", message = "currency must be USD or VND")
    private String currency;

    @NotBlank(message = "billingPeriod is mandatory")
    @Pattern(regexp = "^(MONTHLY|YEARLY)$", message = "billingPeriod must be MONTHLY or YEARLY")
    private String billingPeriod;

    @NotNull(message = "Is active is mandatory")
    private Boolean isActive;

    @NotEmpty(message = "Features is mandatory")
    private Map<String,@Valid UpdatePlanFeatureRequest> features;
}
