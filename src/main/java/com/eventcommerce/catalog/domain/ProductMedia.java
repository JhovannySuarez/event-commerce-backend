package com.eventcommerce.catalog.domain;

import com.eventcommerce.common.domain.MediaType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Table("product_media")
public class ProductMedia {

    @Id
    private UUID id;

    private UUID productId;

    private String mediaUrl;

    private MediaType mediaType;

    private ProductMediaUsageType usageType;

    private String altText;

    private String thumbnailUrl;

    private Integer displayOrder;

    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}