package com.eventcommerce.calendar.controller;

import com.eventcommerce.calendar.dto.EventSpaceAvailabilityResponse;
import com.eventcommerce.calendar.service.EventSpaceAvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/event-spaces")
@RequiredArgsConstructor
public class EventSpaceAvailabilityController {

    private final EventSpaceAvailabilityService availabilityService;

    @GetMapping("/availability")
    public List<EventSpaceAvailabilityResponse> getAvailability(
            @RequestParam UUID venueId,
            @RequestParam Integer guestCount,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startAt,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endAt
    ) {
        return availabilityService.getAvailability(venueId, guestCount, startAt, endAt);
    }
}