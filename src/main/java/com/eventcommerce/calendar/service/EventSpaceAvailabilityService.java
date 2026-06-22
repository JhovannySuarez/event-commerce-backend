package com.eventcommerce.calendar.service;

import com.eventcommerce.calendar.dto.EventSpaceAvailabilityResponse;
import com.eventcommerce.calendar.repository.CalendarEventSpaceRepository;
import com.eventcommerce.common.exception.ResourceNotFoundException;
import com.eventcommerce.venue.domain.EventSpace;
import com.eventcommerce.venue.repository.EventSpaceRepository;
import com.eventcommerce.venue.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventSpaceAvailabilityService {

    private final VenueRepository venueRepository;
    private final EventSpaceRepository eventSpaceRepository;
    private final CalendarEventSpaceRepository calendarEventSpaceRepository;

    public List<EventSpaceAvailabilityResponse> getAvailability(
            UUID venueId,
            Integer guestCount,
            LocalDateTime startAt,
            LocalDateTime endAt
    ) {
        venueRepository.findById(venueId)
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found"));

        return eventSpaceRepository.findByVenueIdAndActiveTrue(venueId)
                .stream()
                .map(space -> toAvailabilityResponse(space, guestCount, startAt, endAt))
                .toList();
    }

    private EventSpaceAvailabilityResponse toAvailabilityResponse(
            EventSpace space,
            Integer guestCount,
            LocalDateTime startAt,
            LocalDateTime endAt
    ) {
        String reason = null;

        if (!supportsGuestCount(space, guestCount)) {
            reason = "Este salón no soporta el número de invitados seleccionado.";
        }

        boolean hasOverlap = calendarEventSpaceRepository.countOverlappingEvents(
                space.getId(),
                startAt,
                endAt
        ) > 0;

        if (reason == null && hasOverlap) {
            reason = "Este salón ya está reservado para la fecha seleccionada.";
        }

        return new EventSpaceAvailabilityResponse(
                space.getId(),
                space.getName(),
                space.getDescription(),
                space.getCapacityMin(),
                space.getCapacityMax(),
                space.getBasePrice(),
                reason == null,
                reason
        );
    }

    private boolean supportsGuestCount(EventSpace space, Integer guestCount) {
        if (guestCount == null) {
            return true;
        }

        if (space.getCapacityMin() != null && guestCount < space.getCapacityMin()) {
            return false;
        }

        return space.getCapacityMax() == null || guestCount <= space.getCapacityMax();
    }
}