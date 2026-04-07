package com.retail.pricing.application.port.in;

import com.retail.pricing.domain.model.Price;


public interface GetPriceUseCase {
    Price execute(GetPriceQuery request);
}
