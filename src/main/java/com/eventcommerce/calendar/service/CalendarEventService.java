package com.eventcommerce.calendar.service;

import com.eventcommerce.calendar.domain.*;
import com.eventcommerce.calendar.dto.CalendarEventResponse;
import com.eventcommerce.calendar.dto.CreateCalendarEventRequest;
import com.eventcommerce.calendar.repository.CalendarEventSpaceRepository;
import com.eventcommerce.calendar.repository.EventTypeRepository;
import com.eventcommerce.calendar.repository.VenueCalendarEventRepository;
import com.eventcommerce.common.exception.BadRequestException;
import com.eventcommerce.common.exception.ResourceNotFoundException;
import com.eventcommerce.tenant.repository.TenantRepository;
import com.eventcommerce.venue.domain.EventSpace;
import com.eventcommerce.venue.domain.Venue;
import com.eventcommerce.venue.repository.EventSpaceRepository;
import com.eventcommerce.venue.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CalendarEventService {

    private final TenantRepository tenantRepository;
    private final VenueRepository venueRepository;
    private final EventSpaceRepository eventSpaceRepository;
    private final EventTypeRepository eventTypeRepository;
    private final VenueCalendarEventRepository venueCalendarEventRepository;
    private final CalendarEventSpaceRepository calendarEventSpaceRepository;

    @Transactional
    public CalendarEventResponse createCalendarEvent(CreateCalendarEventRequest request) {
        validateTenant(request.tenantId());

        Venue venue = venueRepository.findById(request.venueId())
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found"));

        if (!venue.getTenantId().equals(request.tenantId())) {
            throw new BadRequestException("Venue does not belong to tenant");
        }

        EventType eventType = eventTypeRepository.findById(request.eventTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Event type not found"));

        if (!eventType.getTenantId().equals(request.tenantId())) {
            throw new BadRequestException("Event type does not belong to tenant");
        }

        validateDates(request.startAt(), request.endAt());
        validateMinimumDuration(eventType, request.startAt(), request.endAt());

        List<EventSpace> eventSpaces = request.eventSpaceIds()
                .stream()
                .map(this::getEventSpaceOrThrow)
                .toList();

        validateEventSpacesBelongToVenue(eventSpaces, request.venueId());
        validateGuestCapacity(eventSpaces, request.guestCount());
        validateAvailability(eventSpaces, request.startAt(), request.endAt());

        VenueCalendarEvent event = new VenueCalendarEvent();
        event.setTenantId(request.tenantId());
        event.setVenueId(request.venueId());
        event.setEventTypeId(request.eventTypeId());
        event.setTitle(buildTitle(eventType, request.eventDisplayName()));
        event.setEventDisplayName(request.eventDisplayName());
        event.setDescription(request.description());
        event.setGuestCount(request.guestCount());
        event.setStartAt(request.startAt());
        event.setEndAt(request.endAt());
        event.setStatus(CalendarEventStatus.TENTATIVE);
        event.setSource(CalendarEventSource.MANUAL);
        event.setCustomerName(request.customerName());
        event.setCustomerEmail(request.customerEmail());
        event.setCustomerPhone(request.customerPhone());
        event.setInternalNotes(request.internalNotes());
        event.setActive(true);
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());

        VenueCalendarEvent savedEvent = venueCalendarEventRepository.save(event);

        List<UUID> savedSpaceIds = eventSpaces.stream()
                .map(space -> createCalendarEventSpace(savedEvent.getId(), space.getId()))
                .toList();

        return toResponse(savedEvent, savedSpaceIds);
    }

    private void validateTenant(UUID tenantId) {
        tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));
    }

    private EventSpace getEventSpaceOrThrow(UUID eventSpaceId) {
        return eventSpaceRepository.findById(eventSpaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Event space not found"));
    }

    private void validateDates(LocalDateTime startAt, LocalDateTime endAt) {
        if (!endAt.isAfter(startAt)) {
            throw new BadRequestException("endAt must be after startAt");
        }
    }

    private void validateMinimumDuration(EventType eventType, LocalDateTime startAt, LocalDateTime endAt) {
        long durationHours = Duration.between(startAt, endAt).toHours();

        if (durationHours < eventType.getMinimumDurationHours()) {
            throw new BadRequestException(
                    "Event duration must be at least " + eventType.getMinimumDurationHours() + " hours"
            );
        }
    }

    private void validateEventSpacesBelongToVenue(List<EventSpace> eventSpaces, UUID venueId) {
        boolean invalidSpace = eventSpaces.stream()
                .anyMatch(space -> !space.getVenueId().equals(venueId));

        if (invalidSpace) {
            throw new BadRequestException("One or more event spaces do not belong to the selected venue");
        }
    }

    private void validateGuestCapacity(List<EventSpace> eventSpaces, Integer guestCount) {
        for (EventSpace space : eventSpaces) {
            if (space.getCapacityMin() != null && guestCount < space.getCapacityMin()) {
                throw new BadRequestException(
                        "Guest count is below the minimum capacity for event space: " + space.getName()
                );
            }

            if (space.getCapacityMax() != null && guestCount > space.getCapacityMax()) {
                throw new BadRequestException(
                        "Guest count exceeds the maximum capacity for event space: " + space.getName()
                );
            }
        }
    }

    private void validateAvailability(
            List<EventSpace> eventSpaces,
            LocalDateTime startAt,
            LocalDateTime endAt
    ) {
        for (EventSpace space : eventSpaces) {
            int overlaps = calendarEventSpaceRepository.countOverlappingEvents(
                    space.getId(),
                    startAt,
                    endAt
            );

            if (overlaps > 0) {
                throw new BadRequestException("Event space is not available: " + space.getName());
            }
        }
    }

    private UUID createCalendarEventSpace(UUID calendarEventId, UUID eventSpaceId) {
        CalendarEventSpace eventSpace = new CalendarEventSpace();
        eventSpace.setCalendarEventId(calendarEventId);
        eventSpace.setEventSpaceId(eventSpaceId);
        eventSpace.setActive(true);
        eventSpace.setCreatedAt(LocalDateTime.now());

        CalendarEventSpace saved = calendarEventSpaceRepository.save(eventSpace);

        return saved.getEventSpaceId();
    }

    private String buildTitle(EventType eventType, String eventDisplayName) {
        return eventType.getName() + " - " + eventDisplayName;
    }

    private CalendarEventResponse toResponse(VenueCalendarEvent event, List<UUID> eventSpaceIds) {
        return new CalendarEventResponse(
                event.getId(),
                event.getTenantId(),
                event.getVenueId(),
                event.getEventTypeId(),
                eventSpaceIds,
                event.getTitle(),
                event.getEventDisplayName(),
                event.getGuestCount(),
                event.getStartAt(),
                event.getEndAt(),
                event.getStatus().name(),
                event.getSource().name()
        );
    }
}