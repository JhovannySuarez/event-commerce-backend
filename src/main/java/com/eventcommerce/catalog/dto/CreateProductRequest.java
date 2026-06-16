package com.eventcommerce.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateProductRequest(
        @NotNull UUID tenantId,
        @NotNull UUID categoryId,
        @NotBlank String name,
        String shortDescription,
        String longDescription,
        @NotNull BigDecimal price,
        @NotBlank String pricingType
) {
}