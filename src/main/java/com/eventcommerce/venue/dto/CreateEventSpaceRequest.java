package com.eventcommerce.venue.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateEventSpaceRequest(
        @NotNull
        UUID venueId,

        @NotBlank
        @Size(max = 150)
        String name,

        String description,

        @Min(1)
        Integer capacityMin,

        @Min(1)
        Integer capacityMax,

        @DecimalMin(value = "0.0", inclusive = true)
        BigDecimal basePrice
) {
}
