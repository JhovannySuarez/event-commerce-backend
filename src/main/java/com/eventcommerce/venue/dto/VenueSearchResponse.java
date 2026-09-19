package com.eventcommerce.venue.dto;

import com.eventcommerce.venue.domain.SearchTier;

import java.util.List;

public record VenueSearchResponse(
        List<VenueSearchResult> results,
        SearchTier tier,
        boolean hasMore,
        boolean hasNextTier
) {}