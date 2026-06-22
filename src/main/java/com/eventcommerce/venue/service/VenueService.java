package com.eventcommerce.venue.service;

import com.eventcommerce.common.exception.ConflictException;
import com.eventcommerce.common.exception.ResourceNotFoundException;
import com.eventcommerce.tenant.repository.TenantRepository;
import com.eventcommerce.venue.domain.Venue;
import com.eventcommerce.venue.dto.CreateVenueRequest;
import com.eventcommerce.venue.dto.UpdateVenueRequest;
import com.eventcommerce.venue.dto.VenueResponse;
import com.eventcommerce.venue.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VenueService {

    private final VenueRepository venueRepository;
    private final TenantRepository tenantRepository;

    public VenueResponse createVenue(CreateVenueRequest request) {
        tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        String normalizedSlug = normalizeSlug(request.slug());

        venueRepository.findByTenantIdAndSlug(request.tenantId(), normalizedSlug)
                .ifPresent(existingVenue -> {
                    throw new ConflictException("Venue slug already exists for this tenant");
                });

        Venue venue = new Venue();
        venue.setTenantId(request.tenantId());
        venue.setName(request.name());
        venue.setSlug(normalizedSlug);
        venue.setDescription(request.description());
        venue.setAddress(request.address());
        venue.setCity(request.city());
        venue.setCountry(request.country());
        venue.setActive(true);
        venue.setCreatedAt(LocalDateTime.now());
        venue.setUpdatedAt(LocalDateTime.now());

        Venue saved = venueRepository.save(venue);

        return toResponse(saved);
    }

    public List<VenueResponse> getVenuesByTenant(UUID tenantId) {
        tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        return venueRepository.findByTenantId(tenantId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public VenueResponse getVenue(UUID venueId) {
        Venue venue = getVenueOrThrow(venueId);
        return toResponse(venue);
    }

    public VenueResponse getVenueBySlug(UUID tenantId, String slug) {
        String normalizedSlug = normalizeSlug(slug);

        Venue venue = venueRepository.findByTenantIdAndSlug(tenantId, normalizedSlug)
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found"));

        return toResponse(venue);
    }

    public VenueResponse updateVenue(UUID venueId, UpdateVenueRequest request) {
        Venue venue = getVenueOrThrow(venueId);

        String normalizedSlug = normalizeSlug(request.slug());

        venueRepository.findByTenantIdAndSlug(venue.getTenantId(), normalizedSlug)
                .filter(existing -> !existing.getId().equals(venueId))
                .ifPresent(existing -> {
                    throw new ConflictException("Venue slug already exists for this tenant");
                });

        venue.setName(request.name());
        venue.setSlug(normalizedSlug);
        venue.setDescription(request.description());
        venue.setAddress(request.address());
        venue.setCity(request.city());
        venue.setCountry(request.country());
        venue.setActive(request.active());
        venue.setUpdatedAt(LocalDateTime.now());

        Venue updated = venueRepository.save(venue);

        return toResponse(updated);
    }

    public VenueResponse updateVenueStatus(UUID venueId, boolean active) {
        Venue venue = getVenueOrThrow(venueId);

        venue.setActive(active);
        venue.setUpdatedAt(LocalDateTime.now());

        Venue updated = venueRepository.save(venue);

        return toResponse(updated);
    }

    private Venue getVenueOrThrow(UUID venueId) {
        return venueRepository.findById(venueId)
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found"));
    }

    private VenueResponse toResponse(Venue venue) {
        return new VenueResponse(
                venue.getId(),
                venue.getTenantId(),
                venue.getName(),
                venue.getSlug(),
                venue.getDescription(),
                venue.getAddress(),
                venue.getCity(),
                venue.getCountry(),
                venue.isActive(),
                venue.getCreatedAt(),
                venue.getUpdatedAt()
        );
    }

    private String normalizeSlug(String slug) {
        return slug.trim().toLowerCase();
    }
}
