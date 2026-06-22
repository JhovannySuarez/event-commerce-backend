package com.eventcommerce.calendar.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CreateCalendarEventRequest(
        @NotNull UUID tenantId,
        @NotNull UUID venueId,
        @NotNull UUID eventTypeId,
        @NotEmpty List<UUID> eventSpaceIds,
        @NotBlank String eventDisplayName,
        @NotNull @Min(1) Integer guestCount,
        @NotNull LocalDateTime startAt,
        @NotNull LocalDateTime endAt,
        String customerName,
        String customerEmail,
        String customerPhone,
        String description,
        String internalNotes
) {
}