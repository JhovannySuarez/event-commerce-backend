package com.eventcommerce.calendar.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Table("venue_calendar_events")
public class VenueCalendarEvent {

    @Id
    private UUID id;

    private UUID tenantId;

    private UUID venueId;

    private UUID eventTypeId;

    private UUID quotationId;

    private String title;

    private String eventDisplayName;

    private String description;

    private Integer guestCount;

    private LocalDateTime startAt;

    private LocalDateTime endAt;

    private CalendarEventStatus status;

    private CalendarEventSource source;

    private String customerName;

    private String customerEmail;

    private String customerPhone;

    private String internalNotes;

    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}