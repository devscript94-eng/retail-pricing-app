package com.retail.pricing.application.port.in;

import java.time.LocalDateTime;

public record GetPriceQuery(
        LocalDateTime applicationDate,
        Long productId,
        Long brandId
) {}