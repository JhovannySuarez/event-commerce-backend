package com.eventcommerce.venue.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateVenueRequest(
        @NotNull
        UUID tenantId,

        @NotBlank
        @Size(max = 150)
        String name,

        @NotBlank
        @Size(max = 100)
        String slug,

        String description,
        String address,
        String city,
        String country
) {
}