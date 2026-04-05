package com.retail.pricing.domain.repository;

import com.retail.pricing.domain.model.Price;
import com.retail.pricing.domain.model.PriceRequest;

import java.util.Optional;

public interface PriceRepository {
    Optional<Price> findApplicablePrice(PriceRequest request);
}
