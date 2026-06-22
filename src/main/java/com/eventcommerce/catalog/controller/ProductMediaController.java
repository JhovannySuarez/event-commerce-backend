package com.eventcommerce.catalog.controller;

import com.eventcommerce.catalog.dto.CreateProductMediaRequest;
import com.eventcommerce.catalog.dto.ProductMediaResponse;
import com.eventcommerce.catalog.dto.UpdateProductMediaRequest;
import com.eventcommerce.catalog.service.ProductMediaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/product-media")
@RequiredArgsConstructor
public class ProductMediaController {

    private final ProductMediaService productMediaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductMediaResponse createProductMedia(
            @Valid @RequestBody CreateProductMediaRequest request
    ) {
        return productMediaService.createProductMedia(request);
    }

    @GetMapping
    public List<ProductMediaResponse> getProductMedia(
            @RequestParam UUID productId,
            @RequestParam(required = false) String usageType
    ) {
        return productMediaService.getProductMedia(productId, usageType);
    }

    @GetMapping("/{mediaId}")
    public ProductMediaResponse getProductMediaById(
            @PathVariable UUID mediaId
    ) {
        return productMediaService.getProductMediaById(mediaId);
    }

    @PutMapping("/{mediaId}")
    public ProductMediaResponse updateProductMedia(
            @PathVariable UUID mediaId,
            @Valid @RequestBody UpdateProductMediaRequest request
    ) {
        return productMediaService.updateProductMedia(mediaId, request);
    }

    @PatchMapping("/{mediaId}/status")
    public ProductMediaResponse updateProductMediaStatus(
            @PathVariable UUID mediaId,
            @RequestParam boolean active
    ) {
        return productMediaService.updateProductMediaStatus(mediaId, active);
    }

    @DeleteMapping("/{mediaId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProductMedia(
            @PathVariable UUID mediaId
    ) {
        productMediaService.deleteProductMedia(mediaId);
    }
}