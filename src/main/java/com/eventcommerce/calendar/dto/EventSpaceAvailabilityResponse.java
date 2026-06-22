package com.eventcommerce.calendar.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record EventSpaceAvailabilityResponse(
        UUID eventSpaceId,
        String name,
        String description,
        Integer capacityMin,
        Integer capacityMax,
        BigDecimal basePrice,
        boolean available,
        String unavailableReason
) {
}