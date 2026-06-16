package com.eventcommerce.quotation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddQuotationItemRequest(
        @NotNull UUID productId,
        @NotNull @Min(1) Integer quantity
) {
}