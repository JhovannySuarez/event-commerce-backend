package com.eventcommerce.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.UUID;

public record CreateProductMediaRequest(
        @NotNull
        UUID productId,

        @NotBlank
        String mediaUrl,

        @NotBlank
        String mediaType,

        String usageType,

        String altText,

        String thumbnailUrl,

        @PositiveOrZero
        Integer displayOrder
) {
}