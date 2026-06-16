package com.eventcommerce.quotation.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record QuotationResponse(
        UUID id,
        UUID tenantId,
        UUID customerId,
        UUID venueId,
        UUID eventSpaceId,
        String eventType,
        LocalDate eventDate,
        Integer guestCount,
        String status,
        BigDecimal subtotal,
        BigDecimal discountTotal,
        BigDecimal total,
        BigDecimal paidAmount,
        BigDecimal balanceDue
) {
}
