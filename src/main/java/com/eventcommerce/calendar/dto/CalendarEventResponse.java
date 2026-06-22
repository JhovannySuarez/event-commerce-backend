package com.eventcommerce.calendar.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CalendarEventResponse(
        UUID id,
        UUID tenantId,
        UUID venueId,
        UUID eventTypeId,
        List<UUID> eventSpaceIds,
        String title,
        String eventDisplayName,
        Integer guestCount,
        LocalDateTime startAt,
        LocalDateTime endAt,
        String status,
        String source
) {
}