package com.eventcommerce.venue.repository;

import com.eventcommerce.venue.domain.EventSpace;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface EventSpaceRepository extends CrudRepository<EventSpace, UUID> {

    List<EventSpace> findByVenueId(UUID venueId);

    List<EventSpace> findByVenueIdAndActiveTrue(UUID venueId);
}
