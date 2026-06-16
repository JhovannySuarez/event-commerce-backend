package com.eventcommerce.venue.repository;

import com.eventcommerce.venue.domain.Venue;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VenueRepository extends CrudRepository<Venue, UUID> {

    List<Venue> findByTenantId(UUID tenantId);

    Optional<Venue> findByTenantIdAndSlug(UUID tenantId, String slug);
}