package com.eventcommerce.catalog.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProductMediaResponse(
        UUID id,
        UUID productId,
        String mediaUrl,
        String mediaType,
        String usageType,
        String altText,
        String thumbnailUrl,
        Integer displayOrder,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}