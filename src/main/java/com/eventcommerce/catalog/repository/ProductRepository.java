package com.eventcommerce.catalog.repository;

import com.eventcommerce.catalog.domain.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends CrudRepository<Product, UUID> {

    List<Product> findByTenantId(UUID tenantId);

    List<Product> findByTenantIdAndActiveTrue(UUID tenantId);

    List<Product> findByCategoryIdAndActiveTrue(UUID categoryId);

    List<Product> findByTenantIdAndCategoryIdAndActiveTrue(UUID tenantId, UUID categoryId);
}