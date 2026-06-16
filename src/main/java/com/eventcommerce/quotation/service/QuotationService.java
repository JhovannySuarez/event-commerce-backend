package com.eventcommerce.quotation.service;

import com.eventcommerce.payment.domain.PricingType;
import com.eventcommerce.catalog.domain.Product;
import com.eventcommerce.quotation.dto.AddQuotationItemRequest;
import com.eventcommerce.quotation.dto.CreateQuotationRequest;
import com.eventcommerce.quotation.dto.QuotationItemResponse;
import com.eventcommerce.quotation.dto.QuotationResponse;
import com.eventcommerce.catalog.repository.ProductRepository;
import com.eventcommerce.common.exception.BadRequestException;
import com.eventcommerce.common.exception.ResourceNotFoundException;
import com.eventcommerce.quotation.domain.Quotation;
import com.eventcommerce.quotation.domain.QuotationItem;
import com.eventcommerce.quotation.domain.QuotationStatus;
import com.eventcommerce.quotation.repository.QuotationItemRepository;
import com.eventcommerce.quotation.repository.QuotationRepository;
import com.eventcommerce.venue.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuotationService {

    private final QuotationRepository quotationRepository;
    private final QuotationItemRepository quotationItemRepository;
    private final ProductRepository productRepository;
    private final VenueRepository venueRepository;

    public QuotationResponse createQuotation(CreateQuotationRequest request) {
        venueRepository.findById(request.venueId())
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found"));

        Quotation quotation = new Quotation();
        quotation.setTenantId(request.tenantId());
        quotation.setCustomerId(request.customerId());
        quotation.setVenueId(request.venueId());
        quotation.setEventSpaceId(request.eventSpaceId());
        quotation.setEventType(request.eventType());
        quotation.setEventDate(request.eventDate());
        quotation.setGuestCount(request.guestCount());
        quotation.setStatus(QuotationStatus.DRAFT);
        quotation.setSubtotal(BigDecimal.ZERO);
        quotation.setDiscountTotal(BigDecimal.ZERO);
        quotation.setTotal(BigDecimal.ZERO);
        quotation.setPaidAmount(BigDecimal.ZERO);
        quotation.setBalanceDue(BigDecimal.ZERO);
        quotation.setCreatedAt(LocalDateTime.now());
        quotation.setUpdatedAt(LocalDateTime.now());

        Quotation saved = quotationRepository.save(quotation);
        return toResponse(saved);
    }

    public QuotationResponse getQuotation(UUID quotationId) {
        Quotation quotation = getQuotationOrThrow(quotationId);
        return toResponse(quotation);
    }

    @Transactional
    public QuotationItemResponse addItem(UUID quotationId, AddQuotationItemRequest request) {
        Quotation quotation = getQuotationOrThrow(quotationId);

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!quotation.getTenantId().equals(product.getTenantId())) {
            throw new BadRequestException("Product does not belong to quotation tenant");
        }

        BigDecimal unitPrice = resolveUnitPrice(product);
        BigDecimal itemTotal = calculateItemTotal(product, unitPrice, request.quantity());

        QuotationItem item = new QuotationItem();
        item.setQuotationId(quotationId);
        item.setProductId(product.getId());
        item.setCategoryId(product.getCategoryId());
        item.setProductName(product.getName());
        item.setQuantity(request.quantity());
        item.setUnitPrice(unitPrice);
        item.setTotal(itemTotal);
        item.setCreatedAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());

        QuotationItem savedItem = quotationItemRepository.save(item);

        recalculateQuotationTotals(quotationId);

        return toItemResponse(savedItem);
    }

    public List<QuotationItemResponse> getItems(UUID quotationId) {
        return quotationItemRepository.findByQuotationId(quotationId)
                .stream()
                .map(this::toItemResponse)
                .toList();
    }

    @Transactional
    public void deleteItem(UUID quotationId, UUID itemId) {
        getQuotationOrThrow(quotationId);
        quotationItemRepository.deleteByQuotationIdAndId(quotationId, itemId);
        recalculateQuotationTotals(quotationId);
    }

    private BigDecimal resolveUnitPrice(Product product) {
        if (product.getPrice() == null) {
            throw new BadRequestException("Product price is required");
        }

        return product.getPrice();
    }

    private BigDecimal calculateItemTotal(Product product, BigDecimal unitPrice, Integer quantity) {
        if (product.getPricingType() == PricingType.PER_PERSON) {
            return unitPrice.multiply(BigDecimal.valueOf(quantity));
        }

        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    private void recalculateQuotationTotals(UUID quotationId) {
        Quotation quotation = getQuotationOrThrow(quotationId);

        BigDecimal subtotal = quotationItemRepository.findByQuotationId(quotationId)
                .stream()
                .map(QuotationItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal total = subtotal.subtract(quotation.getDiscountTotal());
        BigDecimal balanceDue = total.subtract(quotation.getPaidAmount());

        quotation.setSubtotal(subtotal);
        quotation.setTotal(total);
        quotation.setBalanceDue(balanceDue);
        quotation.setUpdatedAt(LocalDateTime.now());

        quotationRepository.save(quotation);
    }

    private Quotation getQuotationOrThrow(UUID quotationId) {
        return quotationRepository.findById(quotationId)
                .orElseThrow(() -> new ResourceNotFoundException("Quotation not found"));
    }

    private QuotationResponse toResponse(Quotation quotation) {
        return new QuotationResponse(
                quotation.getId(),
                quotation.getTenantId(),
                quotation.getCustomerId(),
                quotation.getVenueId(),
                quotation.getEventSpaceId(),
                quotation.getEventType(),
                quotation.getEventDate(),
                quotation.getGuestCount(),
                quotation.getStatus().name(),
                quotation.getSubtotal(),
                quotation.getDiscountTotal(),
                quotation.getTotal(),
                quotation.getPaidAmount(),
                quotation.getBalanceDue()
        );
    }

    private QuotationItemResponse toItemResponse(QuotationItem item) {
        return new QuotationItemResponse(
                item.getId(),
                item.getProductId(),
                item.getCategoryId(),
                item.getProductName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getTotal()
        );
    }
}
