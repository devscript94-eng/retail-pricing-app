package com.retail.pricing.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Domain representation of a product price that applies for a brand
 * during a given time range.
 */
public record Price(
        Long brandId,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Integer priceList,
        Long productId,
        Integer priority,
        BigDecimal amount,
        String currency
) {}
