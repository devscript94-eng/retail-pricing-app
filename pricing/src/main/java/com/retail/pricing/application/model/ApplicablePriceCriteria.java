package com.retail.pricing.application.model;

import java.time.LocalDateTime;

public record ApplicablePriceCriteria(
        LocalDateTime applicationDate,
        Long productId,
        Long brandId
) {}
