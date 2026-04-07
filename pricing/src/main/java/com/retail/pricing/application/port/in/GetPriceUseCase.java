package com.retail.pricing.application.port.in;

import com.retail.pricing.domain.model.Price;

/**
 * Use case for retrieving the applicable price for a product in a brand
 * at a specific application date.
 */
public interface GetPriceUseCase {
    Price execute(GetPriceQuery request);
}
