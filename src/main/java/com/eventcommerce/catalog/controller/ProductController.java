package com.eventcommerce.catalog.controller;

import com.eventcommerce.catalog.dto.CreateProductRequest;
import com.eventcommerce.catalog.dto.ProductResponse;
import com.eventcommerce.catalog.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ProductResponse createProduct(@Valid @RequestBody CreateProductRequest request) {
        return productService.createProduct(request);
    }

    @GetMapping
    public List<ProductResponse> getProducts(
            @RequestParam UUID tenantId,
            @RequestParam(required = false) UUID categoryId
    ) {
        if (categoryId != null) {
            return productService.getProductsByCategory(tenantId, categoryId);
        }

        return productService.getProductsByTenant(tenantId);
    }

    @GetMapping("/{productId}")
    public ProductResponse getProduct(@PathVariable UUID productId) {
        return productService.getProduct(productId);
    }
}
