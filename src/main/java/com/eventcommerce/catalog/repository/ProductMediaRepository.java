package com.eventcommerce.catalog.repository;

import com.eventcommerce.catalog.domain.ProductMedia;
import com.eventcommerce.catalog.domain.ProductMediaUsageType;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface ProductMediaRepository extends CrudRepository<ProductMedia, UUID> {

    List<ProductMedia> findByProductIdAndActiveTrueOrderByDisplayOrderAsc(UUID productId);

    List<ProductMedia> findByProductIdAndUsageTypeAndActiveTrueOrderByDisplayOrderAsc(
            UUID productId,
            ProductMediaUsageType usageType
    );
}
