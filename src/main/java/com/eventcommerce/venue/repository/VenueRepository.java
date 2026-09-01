package com.eventcommerce.venue.repository;

import com.eventcommerce.venue.domain.Venue;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VenueRepository extends CrudRepository<Venue, UUID> {

    List<Venue> findByTenantId(UUID tenantId);

    Optional<Venue> findByTenantIdAndSlug(UUID tenantId, String slug);

}