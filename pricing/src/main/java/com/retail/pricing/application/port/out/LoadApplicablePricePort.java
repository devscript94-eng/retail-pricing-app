package com.retail.pricing.application.port.out;

import com.retail.pricing.application.model.ApplicablePriceCriteria;
import com.retail.pricing.domain.model.Price;

import java.util.Optional;

public interface LoadApplicablePricePort {
    Optional<Price> findApplicablePrice(ApplicablePriceCriteria criteria);
}
