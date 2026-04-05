package com.retail.pricing.domain.model;

import java.time.LocalDateTime;

public record PriceRequest(
        LocalDateTime applicationDate,
        Long productId,
        Long brandId
) {}