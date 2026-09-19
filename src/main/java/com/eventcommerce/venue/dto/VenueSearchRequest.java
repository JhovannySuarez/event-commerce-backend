package com.eventcommerce.venue.dto;

import com.eventcommerce.venue.domain.SearchTier;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record VenueSearchRequest(
        @NotNull UUID cityId,
        @NotNull UUID eventTypeId,
        @NotNull LocalDate eventDate,
        @NotNull @Min(1) Integer requestedGuests,
        @NotNull SearchTier tier,
        @Min(0) Integer page,
        @Min(1) @Max(50) Integer pageSize
) {}