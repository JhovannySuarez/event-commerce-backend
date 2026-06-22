package com.eventcommerce.calendar.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Table("calendar_event_spaces")
public class CalendarEventSpace {

    @Id
    private UUID id;

    private UUID calendarEventId;

    private UUID eventSpaceId;

    private boolean active;

    private LocalDateTime createdAt;
}