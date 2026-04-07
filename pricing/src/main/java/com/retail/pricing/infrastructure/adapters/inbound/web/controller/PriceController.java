package com.retail.pricing.infrastructure.adapters.inbound.web.controller;

import com.retail.pricing.application.port.in.GetPriceUseCase;
import com.retail.pricing.domain.model.Price;
import com.retail.pricing.application.port.in.GetPriceQuery;
import com.retail.pricing.infrastructure.adapters.inbound.web.dto.PriceResponseDTO;
import com.retail.pricing.infrastructure.adapters.inbound.web.mapper.PriceWebMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/prices")
public class PriceController {

    private final GetPriceUseCase getPriceUseCase;
    private final PriceWebMapper priceWebMapper;

    public PriceController(GetPriceUseCase getPriceUseCase, PriceWebMapper priceWebMapper) {
        this.getPriceUseCase = getPriceUseCase;
        this.priceWebMapper = priceWebMapper;
    }

    @GetMapping
    public ResponseEntity<PriceResponseDTO> getPrice(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime applicationDate,
            @RequestParam Long productId,
            @RequestParam Long brandId) {

        GetPriceQuery request = new GetPriceQuery(applicationDate, productId, brandId);
        Price price = getPriceUseCase.execute(request);

        return ResponseEntity.ok(priceWebMapper.toDto(price));
    }
}