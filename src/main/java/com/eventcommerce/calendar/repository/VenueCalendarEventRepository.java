package com.eventcommerce.calendar.repository;

import com.eventcommerce.calendar.domain.VenueCalendarEvent;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface VenueCalendarEventRepository extends CrudRepository<VenueCalendarEvent, UUID> {

    List<VenueCalendarEvent> findByTenantId(UUID tenantId);

    List<VenueCalendarEvent> findByVenueId(UUID venueId);
}