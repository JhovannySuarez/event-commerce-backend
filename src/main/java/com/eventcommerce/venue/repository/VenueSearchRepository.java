package com.eventcommerce.venue.repository;

import com.eventcommerce.venue.dto.VenueSearchResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface VenueSearchRepository {

    Page<VenueSearchResult> searchQ1(
            UUID eventTypeId,
            UUID cityId,
            LocalDate eventDate,
            int requestedGuests,
            Pageable pageable
    );

    List<VenueSearchResult> searchQ2(
            UUID cityId,
            UUID eventTypeId,
            int requestedGuests,
            LocalDate eventDate,
            int limit,
            int offset,
            Set<UUID> excludedVenueIds);

    List<VenueSearchResult> searchQ3(
            UUID cityId,
            UUID eventTypeId,
            int requestedGuests,
            LocalDate eventDate,
            int limit,
            int offset,
            Set<UUID> excludedVenueIds
    );


}