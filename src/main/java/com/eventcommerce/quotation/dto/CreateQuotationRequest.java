package com.eventcommerce.quotation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record CreateQuotationRequest(
        @NotNull UUID tenantId,
        UUID customerId,
        @NotNull UUID venueId,
        UUID eventSpaceId,
        String eventType,
        LocalDate eventDate,
        @Min(1) Integer guestCount
) {
}