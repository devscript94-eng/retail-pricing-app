package com.retail.pricing.application.service;

import com.retail.pricing.application.exception.PriceNotFoundException;
import com.retail.pricing.application.model.ApplicablePriceCriteria;
import com.retail.pricing.application.port.in.GetPriceQuery;
import com.retail.pricing.application.port.in.GetPriceUseCase;
import com.retail.pricing.application.port.out.LoadApplicablePricePort;
import com.retail.pricing.domain.model.Price;


public class PriceService implements GetPriceUseCase {

    private final LoadApplicablePricePort loadApplicablePricePort;

    public PriceService(LoadApplicablePricePort loadApplicablePricePort) {
        this.loadApplicablePricePort = loadApplicablePricePort;
    }

    @Override
    public Price execute(GetPriceQuery request) {
        ApplicablePriceCriteria criteria = new ApplicablePriceCriteria(
                request.applicationDate(),
                request.productId(),
                request.brandId()
        );

        return loadApplicablePricePort.findApplicablePrice(criteria)
                .orElseThrow(() -> new PriceNotFoundException(
                        request.brandId(),
                        request.productId(),
                        request.applicationDate()
                ));
    }
}
