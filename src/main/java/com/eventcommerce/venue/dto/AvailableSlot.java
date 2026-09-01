package com.eventcommerce.venue.dto;

import java.time.LocalTime;

public record AvailableSlot(
        LocalTime from,
        LocalTime until,
        long hours
) {
}
