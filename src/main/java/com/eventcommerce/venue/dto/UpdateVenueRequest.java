package com.eventcommerce.venue.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateVenueRequest(
        @NotBlank
        @Size(max = 150)
        String name,

        @NotBlank
        @Size(max = 100)
        String slug,

        String description,
        String address,
        String city,
        String country,

        boolean active
) {
}
