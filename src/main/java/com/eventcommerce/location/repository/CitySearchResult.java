package com.eventcommerce.location.repository;

import java.util.UUID;

public record CitySearchResult(
        UUID id,
        String name,
        String state,
        String country
) {
}