package com.eventcommerce.venue.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record AvailableSpace(
        UUID eventSpaceId,
        String name,
        Integer capacityMin,
        Integer capacityMax,
        String bookingMode,
        Integer minimumHours,
        Long availableHours,
        List<AvailableSlot> availableSlots,
        LocalDate availableDate
) {
}