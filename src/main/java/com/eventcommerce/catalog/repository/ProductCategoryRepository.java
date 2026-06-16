package com.eventcommerce.catalog.repository;

import com.eventcommerce.catalog.domain.ProductCategory;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductCategoryRepository extends CrudRepository<ProductCategory, UUID> {

    List<ProductCategory> findByTenantId(UUID tenantId);

    List<ProductCategory> findByTenantIdAndActiveTrueOrderByDisplayOrderAsc(UUID tenantId);

    Optional<ProductCategory> findByTenantIdAndSlug(UUID tenantId, String slug);
}
