package com.eventcommerce.quotation.repository;

import com.eventcommerce.quotation.domain.QuotationPayment;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface QuotationPaymentRepository extends CrudRepository<QuotationPayment, UUID> {

    List<QuotationPayment> findByQuotationId(UUID quotationId);

    List<QuotationPayment> findByTenantId(UUID tenantId);
}