package com.eventcommerce.catalog.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Table("product_categories")
public class ProductCategory {

    @Id
    private UUID id;

    private UUID tenantId;
    private String name;
    private String slug;
    private String description;
    private Integer displayOrder;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
