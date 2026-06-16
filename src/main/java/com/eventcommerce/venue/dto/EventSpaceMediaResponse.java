package com.eventcommerce.venue.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record EventSpaceMediaResponse(

        UUID id,

        UUID eventSpaceId,

        String mediaUrl,

        String mediaType,

        Integer displayOrder,

        boolean active,

        LocalDateTime createdAt,

        LocalDateTime updatedAt

) {
}