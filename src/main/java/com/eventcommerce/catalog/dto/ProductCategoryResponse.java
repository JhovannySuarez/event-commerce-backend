package com.eventcommerce.catalog.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProductCategoryResponse(
        UUID id,
        UUID tenantId,
        String name,
        String slug,
        String description,
        Integer displayOrder,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
