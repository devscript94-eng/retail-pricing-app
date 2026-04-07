package com.retail.pricing.application.port.out;

import com.retail.pricing.application.model.ApplicablePriceCriteria;
import com.retail.pricing.domain.model.Price;

import java.util.Optional;

/**
 * Outbound port used by the application layer to load the price that applies
 * to the given criteria from a persistence source.
 */
public interface LoadApplicablePricePort {
    Optional<Price> findApplicablePrice(ApplicablePriceCriteria criteria);
}
