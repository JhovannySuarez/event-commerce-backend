package com.eventcommerce.venue.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Table("event_space_event_types")
public class EventSpaceEventType {

    @Id
    private UUID id;

    private UUID eventSpaceId;

    private UUID eventTypeId;

    private BookingMode bookingMode;

    private Integer minimumHours;

    private boolean active;

    private LocalDateTime createdAt;
}