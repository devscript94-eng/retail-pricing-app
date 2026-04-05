package com.retail.pricing.infrastructure.adapters.outbound.persistence.adapter;

import com.retail.pricing.domain.model.Price;
import com.retail.pricing.domain.model.PriceRequest;
import com.retail.pricing.domain.repository.PriceRepository;
import com.retail.pricing.infrastructure.adapters.outbound.persistence.mapper.PricePersistenceMapper;
import com.retail.pricing.infrastructure.adapters.outbound.persistence.repository.JpaPriceRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PricePersistenceAdapter implements PriceRepository {

    private final JpaPriceRepository jpaPriceRepository;
    private final PricePersistenceMapper pricePersistenceMapper;

    public PricePersistenceAdapter(JpaPriceRepository jpaPriceRepository,
                                   PricePersistenceMapper pricePersistenceMapper) {
        this.jpaPriceRepository = jpaPriceRepository;
        this.pricePersistenceMapper = pricePersistenceMapper;
    }

    @Override
    public Optional<Price> findApplicablePrice(PriceRequest request) {
        return jpaPriceRepository
                .findFirstByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByPriorityDesc(
                        request.brandId(),
                        request.productId(),
                        request.applicationDate(),
                        request.applicationDate()
                )
                .map(pricePersistenceMapper::toDomain);
    }
}