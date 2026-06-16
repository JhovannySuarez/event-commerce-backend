package com.eventcommerce.venue.controller;

import com.eventcommerce.venue.dto.CreateVenueRequest;
import com.eventcommerce.venue.dto.UpdateVenueRequest;
import com.eventcommerce.venue.dto.VenueResponse;
import com.eventcommerce.venue.service.VenueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/venues")
@RequiredArgsConstructor
public class VenueController {

    private final VenueService venueService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VenueResponse createVenue(@Valid @RequestBody CreateVenueRequest request) {
        return venueService.createVenue(request);
    }

    @GetMapping
    public List<VenueResponse> getVenuesByTenant(@RequestParam UUID tenantId) {
        return venueService.getVenuesByTenant(tenantId);
    }

    @GetMapping("/{venueId}")
    public VenueResponse getVenue(@PathVariable UUID venueId) {
        return venueService.getVenue(venueId);
    }

    @GetMapping("/slug/{slug}")
    public VenueResponse getVenueBySlug(
            @RequestParam UUID tenantId,
            @PathVariable String slug
    ) {
        return venueService.getVenueBySlug(tenantId, slug);
    }

    @PutMapping("/{venueId}")
    public VenueResponse updateVenue(
            @PathVariable UUID venueId,
            @Valid @RequestBody UpdateVenueRequest request
    ) {
        return venueService.updateVenue(venueId, request);
    }

    @PatchMapping("/{venueId}/status")
    public VenueResponse updateVenueStatus(
            @PathVariable UUID venueId,
            @RequestParam boolean active
    ) {
        return venueService.updateVenueStatus(venueId, active);
    }
}
