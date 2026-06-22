package com.eventcommerce.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record UpdateProductMediaRequest(
        @NotBlank
        String mediaUrl,

        @NotBlank
        String mediaType,

        @NotBlank
        String usageType,

        String altText,

        String thumbnailUrl,

        @PositiveOrZero
        Integer displayOrder,

        boolean active
) {
}