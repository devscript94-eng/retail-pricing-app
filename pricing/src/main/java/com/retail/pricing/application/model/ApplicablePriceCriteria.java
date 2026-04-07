package com.retail.pricing.application.model;

import com.retail.pricing.application.port.in.GetPriceQuery;

import java.time.LocalDateTime;

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
