package com.eventcommerce.eventtype.repository;

import com.eventcommerce.eventtype.domain.EventType;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface EventTypeRepository extends CrudRepository<EventType, UUID> {

    List<EventType> findByTenantIdAndActiveTrue(UUID tenantId);
}