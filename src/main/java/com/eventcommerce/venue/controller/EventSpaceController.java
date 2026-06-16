package com.eventcommerce.venue.controller;

import com.eventcommerce.venue.dto.CreateEventSpaceRequest;
import com.eventcommerce.venue.dto.EventSpaceResponse;
import com.eventcommerce.venue.dto.UpdateEventSpaceRequest;
import com.eventcommerce.venue.service.EventSpaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/event-spaces")
@RequiredArgsConstructor
public class EventSpaceController {

    private final EventSpaceService eventSpaceService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventSpaceResponse createEventSpace(
            @Valid @RequestBody CreateEventSpaceRequest request
    ) {
        return eventSpaceService.createEventSpace(request);
    }

    @GetMapping
    public List<EventSpaceResponse> getEventSpacesByVenue(
            @RequestParam UUID venueId
    ) {
        return eventSpaceService.getEventSpacesByVenue(venueId);
    }

    @GetMapping("/{eventSpaceId}")
    public EventSpaceResponse getEventSpace(
            @PathVariable UUID eventSpaceId
    ) {
        return eventSpaceService.getEventSpace(eventSpaceId);
    }

    @PutMapping("/{eventSpaceId}")
    public EventSpaceResponse updateEventSpace(
            @PathVariable UUID eventSpaceId,
            @Valid @RequestBody UpdateEventSpaceRequest request
    ) {
        return eventSpaceService.updateEventSpace(eventSpaceId, request);
    }

    @PatchMapping("/{eventSpaceId}/status")
    public EventSpaceResponse updateEventSpaceStatus(
            @PathVariable UUID eventSpaceId,
            @RequestParam boolean active
    ) {
        return eventSpaceService.updateEventSpaceStatus(eventSpaceId, active);
    }
}
