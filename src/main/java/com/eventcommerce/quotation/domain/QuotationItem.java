package com.eventcommerce.quotation.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Table("quotation_items")
public class QuotationItem {

    @Id
    private UUID id;

    private UUID quotationId;
    private UUID productId;
    private UUID categoryId;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal total;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}