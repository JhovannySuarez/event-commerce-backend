package com.eventcommerce.venue.dto;

import java.util.List;
import java.util.UUID;

public record VenueSearchResult(
        UUID eventSpaceId,
        String eventSpaceName,
        UUID venueId,
        String description,
        String address,
        Double averageRating,
        Integer reviewCount,
        Integer capacityMin,
        Integer capacityMax,
        String bookingMode,
        Integer minimumHours,
        List<AvailableDate> availableDates
) {
}