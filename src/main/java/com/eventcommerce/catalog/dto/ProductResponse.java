package com.eventcommerce.catalog.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        UUID tenantId,
        UUID categoryId,
        String name,
        String shortDescription,
        String longDescription,
        BigDecimal price,
        String pricingType,
        boolean highlighted,
        boolean active
) {
}
