package com.eventcommerce.calendar.repository;

import com.eventcommerce.calendar.domain.EventType;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventTypeRepository extends CrudRepository<EventType, UUID> {

    List<EventType> findByTenantIdAndActiveTrue(UUID tenantId);

    Optional<EventType> findByTenantIdAndName(UUID tenantId, String name);
}