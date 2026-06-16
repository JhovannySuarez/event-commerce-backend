package com.eventcommerce.tenant.controller;

import com.eventcommerce.tenant.dto.CreateTenantRequest;
import com.eventcommerce.tenant.dto.TenantResponse;
import com.eventcommerce.tenant.dto.UpdateTenantRequest;
import com.eventcommerce.tenant.service.TenantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TenantResponse createTenant(@Valid @RequestBody CreateTenantRequest request) {
        return tenantService.createTenant(request);
    }

    @GetMapping
    public List<TenantResponse> getTenants() {
        return tenantService.getTenants();
    }

    @GetMapping("/{tenantId}")
    public TenantResponse getTenant(@PathVariable UUID tenantId) {
        return tenantService.getTenant(tenantId);
    }

    @GetMapping("/slug/{slug}")
    public TenantResponse getTenantBySlug(@PathVariable String slug) {
        return tenantService.getTenantBySlug(slug);
    }

    @PutMapping("/{tenantId}")
    public TenantResponse updateTenant(
            @PathVariable UUID tenantId,
            @Valid @RequestBody UpdateTenantRequest request
    ) {
        return tenantService.updateTenant(tenantId, request);
    }

    @PatchMapping("/{tenantId}/status")
    public TenantResponse updateTenantStatus(
            @PathVariable UUID tenantId,
            @RequestParam boolean active
    ) {
        return tenantService.updateTenantStatus(tenantId, active);
    }
}