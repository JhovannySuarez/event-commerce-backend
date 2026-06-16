package com.eventcommerce.tenant.repository;

import com.eventcommerce.tenant.domain.Tenant;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface TenantRepository extends CrudRepository<Tenant, UUID> {

    Optional<Tenant> findBySlug(String slug);
}