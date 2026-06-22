package com.eventcommerce.calendar.controller;

import com.eventcommerce.calendar.dto.CalendarEventResponse;
import com.eventcommerce.calendar.dto.CreateCalendarEventRequest;
import com.eventcommerce.calendar.service.CalendarEventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/calendar-events")
@RequiredArgsConstructor
public class CalendarEventController {

    private final CalendarEventService calendarEventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CalendarEventResponse createCalendarEvent(
            @Valid @RequestBody CreateCalendarEventRequest request
    ) {
        return calendarEventService.createCalendarEvent(request);
    }
}