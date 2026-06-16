package com.eventcommerce.venue.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record VenueResponse(
        UUID id,
        UUID tenantId,
        String name,
        String slug,
        String description,
        String address,
        String city,
        String country,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
