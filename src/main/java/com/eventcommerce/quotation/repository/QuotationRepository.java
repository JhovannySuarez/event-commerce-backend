package com.eventcommerce.quotation.repository;

import com.eventcommerce.quotation.domain.Quotation;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface QuotationRepository extends CrudRepository<Quotation, UUID> {

    List<Quotation> findByTenantId(UUID tenantId);

    List<Quotation> findByCustomerId(UUID customerId);

    List<Quotation> findByVenueId(UUID venueId);
}