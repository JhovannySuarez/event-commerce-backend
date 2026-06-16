package com.eventcommerce.quotation.controller;

import com.eventcommerce.quotation.dto.AddQuotationItemRequest;
import com.eventcommerce.quotation.dto.CreateQuotationRequest;
import com.eventcommerce.quotation.dto.QuotationItemResponse;
import com.eventcommerce.quotation.dto.QuotationResponse;
import com.eventcommerce.quotation.service.QuotationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/quotations")
@RequiredArgsConstructor
public class QuotationController {

    private final QuotationService quotationService;

    @PostMapping
    public QuotationResponse createQuotation(@Valid @RequestBody CreateQuotationRequest request) {
        return quotationService.createQuotation(request);
    }

    @GetMapping("/{quotationId}")
    public QuotationResponse getQuotation(@PathVariable UUID quotationId) {
        return quotationService.getQuotation(quotationId);
    }

    @PostMapping("/{quotationId}/items")
    public QuotationItemResponse addItem(
            @PathVariable UUID quotationId,
            @Valid @RequestBody AddQuotationItemRequest request
    ) {
        return quotationService.addItem(quotationId, request);
    }

    @GetMapping("/{quotationId}/items")
    public List<QuotationItemResponse> getItems(@PathVariable UUID quotationId) {
        return quotationService.getItems(quotationId);
    }

    @DeleteMapping("/{quotationId}/items/{itemId}")
    public void deleteItem(
            @PathVariable UUID quotationId,
            @PathVariable UUID itemId
    ) {
        quotationService.deleteItem(quotationId, itemId);
    }
}