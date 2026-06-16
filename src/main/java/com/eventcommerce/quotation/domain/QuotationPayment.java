package com.eventcommerce.quotation.domain;

import com.eventcommerce.payment.domain.PaymentMethod;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Table("quotation_payments")
public class QuotationPayment {

    @Id
    private UUID id;

    private UUID tenantId;
    private UUID quotationId;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private LocalDate paymentDate;
    private String reference;
    private String notes;
    private String receiptUrl;
    private UUID registeredByUserId;
    private LocalDateTime createdAt;
}