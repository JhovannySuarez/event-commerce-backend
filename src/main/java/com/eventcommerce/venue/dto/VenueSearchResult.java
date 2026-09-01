package com.eventcommerce.venue.dto;

import java.util.List;
import java.util.UUID;

public record VenueSearchResult(
        UUID venueId,
        String venueName,
        String slug,
        String description,
        String address,
        String city,
        Double latitude,
        Double longitude,
        Double averageRating,
        Integer reviewCount,
        List<AvailableSpace> availableSpaces,
        List<AvailableDate> availableDates
) {
}