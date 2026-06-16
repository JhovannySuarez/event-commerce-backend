package com.eventcommerce.catalog.service;

import com.eventcommerce.catalog.domain.ProductCategory;
import com.eventcommerce.catalog.dto.CreateProductCategoryRequest;
import com.eventcommerce.catalog.dto.ProductCategoryResponse;
import com.eventcommerce.catalog.dto.UpdateProductCategoryRequest;
import com.eventcommerce.catalog.repository.ProductCategoryRepository;
import com.eventcommerce.common.exception.ConflictException;
import com.eventcommerce.common.exception.ResourceNotFoundException;
import com.eventcommerce.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductCategoryService {

    private final ProductCategoryRepository productCategoryRepository;
    private final TenantRepository tenantRepository;

    public ProductCategoryResponse createCategory(CreateProductCategoryRequest request) {
        tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        String normalizedSlug = normalizeSlug(request.slug());

        productCategoryRepository.findByTenantIdAndSlug(request.tenantId(), normalizedSlug)
                .ifPresent(category -> {
                    throw new ConflictException("Product category slug already exists for this tenant");
                });

        ProductCategory category = new ProductCategory();
        category.setTenantId(request.tenantId());
        category.setName(request.name());
        category.setSlug(normalizedSlug);
        category.setDescription(request.description());
        category.setDisplayOrder(request.displayOrder() != null ? request.displayOrder() : 0);
        category.setActive(true);
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());

        ProductCategory saved = productCategoryRepository.save(category);

        return toResponse(saved);
    }

    public List<ProductCategoryResponse> getCategoriesByTenant(UUID tenantId) {
        tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        return productCategoryRepository.findByTenantIdAndActiveTrueOrderByDisplayOrderAsc(tenantId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductCategoryResponse getCategory(UUID categoryId) {
        ProductCategory category = getCategoryOrThrow(categoryId);
        return toResponse(category);
    }

    public ProductCategoryResponse updateCategory(UUID categoryId, UpdateProductCategoryRequest request) {
        ProductCategory category = getCategoryOrThrow(categoryId);

        String normalizedSlug = normalizeSlug(request.slug());

        productCategoryRepository.findByTenantIdAndSlug(category.getTenantId(), normalizedSlug)
                .filter(existing -> !existing.getId().equals(categoryId))
                .ifPresent(existing -> {
                    throw new ConflictException("Product category slug already exists for this tenant");
                });

        category.setName(request.name());
        category.setSlug(normalizedSlug);
        category.setDescription(request.description());
        category.setDisplayOrder(request.displayOrder() != null ? request.displayOrder() : 0);
        category.setActive(request.active());
        category.setUpdatedAt(LocalDateTime.now());

        ProductCategory updated = productCategoryRepository.save(category);

        return toResponse(updated);
    }

    public ProductCategoryResponse updateCategoryStatus(UUID categoryId, boolean active) {
        ProductCategory category = getCategoryOrThrow(categoryId);

        category.setActive(active);
        category.setUpdatedAt(LocalDateTime.now());

        ProductCategory updated = productCategoryRepository.save(category);

        return toResponse(updated);
    }

    private ProductCategory getCategoryOrThrow(UUID categoryId) {
        return productCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Product category not found"));
    }

    private ProductCategoryResponse toResponse(ProductCategory category) {
        return new ProductCategoryResponse(
                category.getId(),
                category.getTenantId(),
                category.getName(),
                category.getSlug(),
                category.getDescription(),
                category.getDisplayOrder(),
                category.isActive(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }

    private String normalizeSlug(String slug) {
        return slug.trim().toLowerCase();
    }
}