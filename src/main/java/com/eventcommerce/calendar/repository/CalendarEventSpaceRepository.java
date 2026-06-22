package com.eventcommerce.calendar.repository;

import com.eventcommerce.calendar.domain.CalendarEventSpace;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface CalendarEventSpaceRepository extends CrudRepository<CalendarEventSpace, UUID> {

    List<CalendarEventSpace> findByCalendarEventIdAndActiveTrue(UUID calendarEventId);

    @Query("""
        SELECT COUNT(1)
        FROM venue_calendar_events e
        JOIN calendar_event_spaces ces
            ON ces.calendar_event_id = e.id
        WHERE ces.event_space_id = :eventSpaceId
          AND e.active = TRUE
          AND ces.active = TRUE
          AND e.status IN ('TENTATIVE', 'CONFIRMED', 'BLOCKED')
          AND e.start_at < :requestedEndAt
          AND e.end_at > :requestedStartAt
        """)
    int countOverlappingEvents(
            @Param("eventSpaceId") UUID eventSpaceId,
            @Param("requestedStartAt") LocalDateTime requestedStartAt,
            @Param("requestedEndAt") LocalDateTime requestedEndAt
    );
}