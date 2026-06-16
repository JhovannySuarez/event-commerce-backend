package com.eventcommerce.venue.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Table("event_space_media")
public class EventSpaceMedia {

    @Id
    private UUID id;

    private UUID eventSpaceId;

    /**
     * Azure Blob URL, S3 URL, etc.
     */
    private String mediaUrl;

    /**
     * IMAGE, VIDEO
     */
    private MediaType mediaType;

    /**
     * Controls the order shown in the UI.
     */
    private Integer displayOrder;

    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}