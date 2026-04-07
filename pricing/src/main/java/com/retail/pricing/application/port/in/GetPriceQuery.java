package com.retail.pricing.application.port.in;

import java.time.LocalDateTime;

/**
 * Input data required to determine the applicable price for a product
 * in a brand at a specific date and time.
 */
public record GetPriceQuery(
        LocalDateTime applicationDate,
        Long productId,
        Long brandId
) {}