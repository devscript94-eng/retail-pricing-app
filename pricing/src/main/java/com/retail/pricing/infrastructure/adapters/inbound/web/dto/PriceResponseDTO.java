package com.retail.pricing.infrastructure.adapters.inbound.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PriceResponseDTO(
        Long productId,
        Long brandId,
        Integer priceList,
        LocalDateTime startDate,
        LocalDateTime endDate,
        BigDecimal finalPrice
) {}
