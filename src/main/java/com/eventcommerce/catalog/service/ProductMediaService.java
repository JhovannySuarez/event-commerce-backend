package com.eventcommerce.catalog.service;

import com.eventcommerce.catalog.domain.Product;
import com.eventcommerce.catalog.domain.ProductMedia;
import com.eventcommerce.catalog.domain.ProductMediaUsageType;
import com.eventcommerce.catalog.dto.CreateProductMediaRequest;
import com.eventcommerce.catalog.dto.ProductMediaResponse;
import com.eventcommerce.catalog.dto.UpdateProductMediaRequest;
import com.eventcommerce.catalog.repository.ProductMediaRepository;
import com.eventcommerce.catalog.repository.ProductRepository;
import com.eventcommerce.common.domain.MediaType;
import com.eventcommerce.common.exception.BadRequestException;
import com.eventcommerce.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductMediaService {

    private final ProductMediaRepository productMediaRepository;
    private final ProductRepository productRepository;

    public ProductMediaResponse createProductMedia(CreateProductMediaRequest request) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        MediaType mediaType = parseMediaType(request.mediaType());
        ProductMediaUsageType usageType = parseUsageTypeOrDefault(request.usageType());

        ProductMedia productMedia = new ProductMedia();
        productMedia.setProductId(product.getId());
        productMedia.setMediaUrl(request.mediaUrl());
        productMedia.setMediaType(mediaType);
        productMedia.setUsageType(usageType);
        productMedia.setAltText(request.altText());
        productMedia.setThumbnailUrl(request.thumbnailUrl());
        productMedia.setDisplayOrder(request.displayOrder() != null ? request.displayOrder() : 0);
        productMedia.setActive(true);
        productMedia.setCreatedAt(LocalDateTime.now());
        productMedia.setUpdatedAt(LocalDateTime.now());

        ProductMedia saved = productMediaRepository.save(productMedia);

        return toResponse(saved);
    }

    public List<ProductMediaResponse> getProductMedia(UUID productId, String usageType) {
        productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (usageType != null && !usageType.isBlank()) {
            ProductMediaUsageType parsedUsageType = parseUsageType(usageType);

            return productMediaRepository
                    .findByProductIdAndUsageTypeAndActiveTrueOrderByDisplayOrderAsc(productId, parsedUsageType)
                    .stream()
                    .map(this::toResponse)
                    .toList();
        }

        return productMediaRepository.findByProductIdAndActiveTrueOrderByDisplayOrderAsc(productId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductMediaResponse getProductMediaById(UUID mediaId) {
        ProductMedia productMedia = getProductMediaOrThrow(mediaId);
        return toResponse(productMedia);
    }

    public ProductMediaResponse updateProductMedia(UUID mediaId, UpdateProductMediaRequest request) {
        ProductMedia productMedia = getProductMediaOrThrow(mediaId);

        productMedia.setMediaUrl(request.mediaUrl());
        productMedia.setMediaType(parseMediaType(request.mediaType()));
        productMedia.setUsageType(parseUsageTypeOrDefault(request.usageType()));
        productMedia.setAltText(request.altText());
        productMedia.setThumbnailUrl(request.thumbnailUrl());
        productMedia.setDisplayOrder(request.displayOrder() != null ? request.displayOrder() : 0);
        productMedia.setActive(request.active());
        productMedia.setUpdatedAt(LocalDateTime.now());

        ProductMedia updated = productMediaRepository.save(productMedia);

        return toResponse(updated);
    }

    public ProductMediaResponse updateProductMediaStatus(UUID mediaId, boolean active) {
        ProductMedia productMedia = getProductMediaOrThrow(mediaId);

        productMedia.setActive(active);
        productMedia.setUpdatedAt(LocalDateTime.now());

        ProductMedia updated = productMediaRepository.save(productMedia);

        return toResponse(updated);
    }

    public void deleteProductMedia(UUID mediaId) {
        ProductMedia productMedia = getProductMediaOrThrow(mediaId);
        productMedia.setActive(false);
        productMedia.setUpdatedAt(LocalDateTime.now());

        productMediaRepository.save(productMedia);
    }

    private ProductMedia getProductMediaOrThrow(UUID mediaId) {
        return productMediaRepository.findById(mediaId)
                .orElseThrow(() -> new ResourceNotFoundException("Product media not found"));
    }

    private MediaType parseMediaType(String value) {
        try {
            return MediaType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new BadRequestException("Invalid mediaType. Valid values: IMAGE, VIDEO");
        }
    }

    private ProductMediaUsageType parseUsageTypeOrDefault(String value) {
        if (value == null || value.isBlank()) {
            return ProductMediaUsageType.GALLERY;
        }

        return parseUsageType(value);
    }

    private ProductMediaUsageType parseUsageType(String value) {
        try {
            return ProductMediaUsageType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new BadRequestException("Invalid usageType. Valid values: LISTING, GALLERY, DETAIL, THUMBNAIL");
        }
    }

    private ProductMediaResponse toResponse(ProductMedia productMedia) {
        return new ProductMediaResponse(
                productMedia.getId(),
                productMedia.getProductId(),
                productMedia.getMediaUrl(),
                productMedia.getMediaType().name(),
                productMedia.getUsageType().name(),
                productMedia.getAltText(),
                productMedia.getThumbnailUrl(),
                productMedia.getDisplayOrder(),
                productMedia.isActive(),
                productMedia.getCreatedAt(),
                productMedia.getUpdatedAt()
        );
    }
}