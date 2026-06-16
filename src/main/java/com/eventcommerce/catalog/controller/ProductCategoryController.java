package com.eventcommerce.catalog.controller;

import com.eventcommerce.catalog.dto.CreateProductCategoryRequest;
import com.eventcommerce.catalog.dto.ProductCategoryResponse;
import com.eventcommerce.catalog.dto.UpdateProductCategoryRequest;
import com.eventcommerce.catalog.service.ProductCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class ProductCategoryController {

    private final ProductCategoryService productCategoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductCategoryResponse createCategory(
            @Valid @RequestBody CreateProductCategoryRequest request
    ) {
        return productCategoryService.createCategory(request);
    }

    @GetMapping
    public List<ProductCategoryResponse> getCategoriesByTenant(
            @RequestParam UUID tenantId
    ) {
        return productCategoryService.getCategoriesByTenant(tenantId);
    }

    @GetMapping("/{categoryId}")
    public ProductCategoryResponse getCategory(
            @PathVariable UUID categoryId
    ) {
        return productCategoryService.getCategory(categoryId);
    }

    @PutMapping("/{categoryId}")
    public ProductCategoryResponse updateCategory(
            @PathVariable UUID categoryId,
            @Valid @RequestBody UpdateProductCategoryRequest request
    ) {
        return productCategoryService.updateCategory(categoryId, request);
    }

    @PatchMapping("/{categoryId}/status")
    public ProductCategoryResponse updateCategoryStatus(
            @PathVariable UUID categoryId,
            @RequestParam boolean active
    ) {
        return productCategoryService.updateCategoryStatus(categoryId, active);
    }
}