package com.eventcommerce.venue.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record VenueQ2Availability(
        UUID venueId,
        LocalDate closestAvailableDate,
        List<AvailableDate> availableDates
) {
}
