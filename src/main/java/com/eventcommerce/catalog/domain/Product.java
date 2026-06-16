package com.eventcommerce.catalog.domain;

import com.eventcommerce.payment.domain.PricingType;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Table("products")
public class Product {

    @Id
    private UUID id;

    private UUID tenantId;
    private UUID categoryId;
    private String name;
    private String shortDescription;
    private String longDescription;
    private BigDecimal price;
    private PricingType pricingType;
    private String includedItems;
    private String excludedItems;
    private String requirements;
    private String restrictions;
    private Integer durationMinutes;
    private boolean requiresApproval;
    private boolean highlighted;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}