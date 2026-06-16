package com.eventcommerce.quotation.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Table("quotations")
public class Quotation {

    @Id
    private UUID id;

    private UUID tenantId;
    private UUID customerId;
    private UUID venueId;
    private UUID eventSpaceId;
    private String eventType;
    private LocalDate eventDate;
    private Integer guestCount;
    private QuotationStatus status;
    private BigDecimal subtotal;
    private BigDecimal discountTotal;
    private BigDecimal total;
    private BigDecimal paidAmount;
    private BigDecimal balanceDue;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
