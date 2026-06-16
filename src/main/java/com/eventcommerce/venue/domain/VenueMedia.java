package com.eventcommerce.venue.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Table("venue_media")
public class VenueMedia {

    @Id
    private UUID id;

    private UUID venueId;

    private String mediaUrl;

    private MediaType mediaType;

    private Integer displayOrder;

    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}