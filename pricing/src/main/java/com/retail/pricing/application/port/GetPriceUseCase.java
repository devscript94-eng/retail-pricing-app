package com.retail.pricing.application.port;

import com.retail.pricing.domain.model.Price;
import com.retail.pricing.domain.model.PriceRequest;


public interface GetPriceUseCase {
    Price execute(PriceRequest request);
}
