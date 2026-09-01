package com.eventcommerce.venue.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@Table("event_space_operating_hours")
public class EventSpaceOperatingHours {

    @Id
    private UUID id;

    private UUID eventSpaceId;

    private String dayOfWeek;

    private LocalTime operatingFrom;

    private LocalTime operatingUntil;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}