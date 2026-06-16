package com.eventcommerce.venue.repository;

import com.eventcommerce.venue.domain.EventSpaceMedia;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface EventSpaceMediaRepository
        extends CrudRepository<EventSpaceMedia, UUID> {

    List<EventSpaceMedia> findByEventSpaceIdAndActiveTrueOrderByDisplayOrderAsc(
            UUID eventSpaceId);

}