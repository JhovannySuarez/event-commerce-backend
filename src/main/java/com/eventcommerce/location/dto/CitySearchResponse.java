package com.eventcommerce.location.dto;

import java.util.UUID;

public record CitySearchResponse(
        UUID id,
        String name,
        String location
) {
}