package com.eventcommerce.venue.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateEventSpaceMediaRequest(

        @NotNull
        UUID eventSpaceId,

        @NotBlank
        String mediaUrl,

        @NotBlank
        String mediaType,

        Integer displayOrder

) {
}