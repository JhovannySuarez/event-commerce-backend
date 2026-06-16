package com.eventcommerce.tenant.service;

import com.eventcommerce.common.exception.ConflictException;
import com.eventcommerce.common.exception.ResourceNotFoundException;
import com.eventcommerce.tenant.domain.Tenant;
import com.eventcommerce.tenant.dto.CreateTenantRequest;
import com.eventcommerce.tenant.dto.TenantResponse;
import com.eventcommerce.tenant.dto.UpdateTenantRequest;
import com.eventcommerce.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;

    public TenantResponse createTenant(CreateTenantRequest request) {
        validateSlugIsAvailable(request.slug());

        Tenant tenant = new Tenant();
        tenant.setName(request.name());
        tenant.setSlug(normalizeSlug(request.slug()));
        tenant.setActive(true);
        tenant.setCreatedAt(LocalDateTime.now());
        tenant.setUpdatedAt(LocalDateTime.now());

        Tenant savedTenant = tenantRepository.save(tenant);

        return toResponse(savedTenant);
    }

    public List<TenantResponse> getTenants() {
        return StreamSupport.stream(tenantRepository.findAll().spliterator(), false)
                .map(this::toResponse)
                .toList();
    }

    public TenantResponse getTenant(UUID tenantId) {
        Tenant tenant = getTenantOrThrow(tenantId);
        return toResponse(tenant);
    }

    public TenantResponse getTenantBySlug(String slug) {
        Tenant tenant = tenantRepository.findBySlug(normalizeSlug(slug))
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        return toResponse(tenant);
    }

    public TenantResponse updateTenant(UUID tenantId, UpdateTenantRequest request) {
        Tenant tenant = getTenantOrThrow(tenantId);

        String normalizedSlug = normalizeSlug(request.slug());

        tenantRepository.findBySlug(normalizedSlug)
                .filter(existingTenant -> !existingTenant.getId().equals(tenantId))
                .ifPresent(existingTenant -> {
                    throw new ConflictException("Tenant slug already exists");
                });

        tenant.setName(request.name());
        tenant.setSlug(normalizedSlug);
        tenant.setActive(request.active());
        tenant.setUpdatedAt(LocalDateTime.now());

        Tenant updatedTenant = tenantRepository.save(tenant);

        return toResponse(updatedTenant);
    }

    public TenantResponse updateTenantStatus(UUID tenantId, boolean active) {
        Tenant tenant = getTenantOrThrow(tenantId);

        tenant.setActive(active);
        tenant.setUpdatedAt(LocalDateTime.now());

        Tenant updatedTenant = tenantRepository.save(tenant);

        return toResponse(updatedTenant);
    }

    private void validateSlugIsAvailable(String slug) {
        tenantRepository.findBySlug(normalizeSlug(slug))
                .ifPresent(existingTenant -> {
                    throw new ConflictException("Tenant slug already exists");
                });
    }

    private Tenant getTenantOrThrow(UUID tenantId) {
        return tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));
    }

    private TenantResponse toResponse(Tenant tenant) {
        return new TenantResponse(
                tenant.getId(),
                tenant.getName(),
                tenant.getSlug(),
                tenant.isActive(),
                tenant.getCreatedAt(),
                tenant.getUpdatedAt()
        );
    }

    private String normalizeSlug(String slug) {
        return slug.trim().toLowerCase();
    }
}