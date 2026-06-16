package com.eventcommerce.venue.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record EventSpaceResponse(
        UUID id,
        UUID venueId,
        String name,
        String description,
        Integer capacityMin,
        Integer capacityMax,
        BigDecimal basePrice,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}