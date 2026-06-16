package com.eventcommerce.venue.service;

import com.eventcommerce.common.exception.BadRequestException;
import com.eventcommerce.common.exception.ResourceNotFoundException;
import com.eventcommerce.venue.domain.EventSpace;
import com.eventcommerce.venue.domain.Venue;
import com.eventcommerce.venue.dto.CreateEventSpaceRequest;
import com.eventcommerce.venue.dto.EventSpaceResponse;
import com.eventcommerce.venue.dto.UpdateEventSpaceRequest;
import com.eventcommerce.venue.repository.EventSpaceRepository;
import com.eventcommerce.venue.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventSpaceService {

    private final EventSpaceRepository eventSpaceRepository;
    private final VenueRepository venueRepository;

    public EventSpaceResponse createEventSpace(CreateEventSpaceRequest request) {
        Venue venue = venueRepository.findById(request.venueId())
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found"));

        validateCapacity(request.capacityMin(), request.capacityMax());

        EventSpace eventSpace = new EventSpace();
        eventSpace.setVenueId(venue.getId());
        eventSpace.setName(request.name());
        eventSpace.setDescription(request.description());
        eventSpace.setCapacityMin(request.capacityMin());
        eventSpace.setCapacityMax(request.capacityMax());
        eventSpace.setBasePrice(request.basePrice());
        eventSpace.setActive(true);
        eventSpace.setCreatedAt(LocalDateTime.now());
        eventSpace.setUpdatedAt(LocalDateTime.now());

        EventSpace saved = eventSpaceRepository.save(eventSpace);

        return toResponse(saved);
    }

    public List<EventSpaceResponse> getEventSpacesByVenue(UUID venueId) {
        venueRepository.findById(venueId)
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found"));

        return eventSpaceRepository.findByVenueIdAndActiveTrue(venueId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public EventSpaceResponse getEventSpace(UUID eventSpaceId) {
        EventSpace eventSpace = getEventSpaceOrThrow(eventSpaceId);
        return toResponse(eventSpace);
    }

    public EventSpaceResponse updateEventSpace(UUID eventSpaceId, UpdateEventSpaceRequest request) {
        EventSpace eventSpace = getEventSpaceOrThrow(eventSpaceId);

        validateCapacity(request.capacityMin(), request.capacityMax());

        eventSpace.setName(request.name());
        eventSpace.setDescription(request.description());
        eventSpace.setCapacityMin(request.capacityMin());
        eventSpace.setCapacityMax(request.capacityMax());
        eventSpace.setBasePrice(request.basePrice());
        eventSpace.setActive(request.active());
        eventSpace.setUpdatedAt(LocalDateTime.now());

        EventSpace updated = eventSpaceRepository.save(eventSpace);

        return toResponse(updated);
    }

    public EventSpaceResponse updateEventSpaceStatus(UUID eventSpaceId, boolean active) {
        EventSpace eventSpace = getEventSpaceOrThrow(eventSpaceId);

        eventSpace.setActive(active);
        eventSpace.setUpdatedAt(LocalDateTime.now());

        EventSpace updated = eventSpaceRepository.save(eventSpace);

        return toResponse(updated);
    }

    private EventSpace getEventSpaceOrThrow(UUID eventSpaceId) {
        return eventSpaceRepository.findById(eventSpaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Event space not found"));
    }

    private void validateCapacity(Integer capacityMin, Integer capacityMax) {
        if (capacityMin != null && capacityMax != null && capacityMin > capacityMax) {
            throw new BadRequestException("capacityMin cannot be greater than capacityMax");
        }
    }

    private EventSpaceResponse toResponse(EventSpace eventSpace) {
        return new EventSpaceResponse(
                eventSpace.getId(),
                eventSpace.getVenueId(),
                eventSpace.getName(),
                eventSpace.getDescription(),
                eventSpace.getCapacityMin(),
                eventSpace.getCapacityMax(),
                eventSpace.getBasePrice(),
                eventSpace.isActive(),
                eventSpace.getCreatedAt(),
                eventSpace.getUpdatedAt()
        );
    }
}
