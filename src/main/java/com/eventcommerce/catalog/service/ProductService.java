package com.eventcommerce.catalog.service;

import com.eventcommerce.payment.domain.PricingType;
import com.eventcommerce.catalog.domain.Product;
import com.eventcommerce.catalog.dto.CreateProductRequest;
import com.eventcommerce.catalog.dto.ProductResponse;
import com.eventcommerce.catalog.repository.ProductCategoryRepository;
import com.eventcommerce.catalog.repository.ProductRepository;
import com.eventcommerce.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;

    public ProductResponse createProduct(CreateProductRequest request) {
        productCategoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Product category not found"));

        Product product = new Product();
        product.setTenantId(request.tenantId());
        product.setCategoryId(request.categoryId());
        product.setName(request.name());
        product.setShortDescription(request.shortDescription());
        product.setLongDescription(request.longDescription());
        product.setPrice(request.price());
        product.setPricingType(PricingType.valueOf(request.pricingType()));
        product.setHighlighted(false);
        product.setActive(true);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        Product saved = productRepository.save(product);
        return toResponse(saved);
    }

    public List<ProductResponse> getProductsByTenant(UUID tenantId) {
        return productRepository.findByTenantIdAndActiveTrue(tenantId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ProductResponse> getProductsByCategory(UUID tenantId, UUID categoryId) {
        return productRepository.findByTenantIdAndCategoryIdAndActiveTrue(tenantId, categoryId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductResponse getProduct(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        return toResponse(product);
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getTenantId(),
                product.getCategoryId(),
                product.getName(),
                product.getShortDescription(),
                product.getLongDescription(),
                product.getPrice(),
                product.getPricingType().name(),
                product.isHighlighted(),
                product.isActive()
        );
    }
}