package com.retail.pricing.application.model;

import com.retail.pricing.application.port.in.GetPriceQuery;

import java.time.LocalDateTime;

/**
 * Application-level criteria used by outbound adapters to search for
 * the price that applies at a specific date and time.
 */
public record ApplicablePriceCriteria(
        LocalDateTime applicationDate,
        Long productId,
        Long brandId
) {
    public static ApplicablePriceCriteria from(GetPriceQuery query) {
        return new ApplicablePriceCriteria(
                query.applicationDate(),
                query.productId(),
                query.brandId()
        );
    }
}
