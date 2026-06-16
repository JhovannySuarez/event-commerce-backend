package com.eventcommerce.quotation.repository;

import com.eventcommerce.quotation.domain.QuotationItem;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface QuotationItemRepository extends CrudRepository<QuotationItem, UUID> {

    List<QuotationItem> findByQuotationId(UUID quotationId);

    void deleteByQuotationIdAndId(UUID quotationId, UUID id);
}
