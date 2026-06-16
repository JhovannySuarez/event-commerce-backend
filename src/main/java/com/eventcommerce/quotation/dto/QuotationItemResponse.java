package com.eventcommerce.quotation.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record QuotationItemResponse(
        UUID id,
        UUID productId,
        UUID categoryId,
        String productName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal total
) {
}
