package com.retail.pricing.infrastructure.adapters.outbound.persistence.adapter;

import com.retail.pricing.application.model.ApplicablePriceCriteria;
import com.retail.pricing.domain.model.Price;
import com.retail.pricing.application.port.out.LoadApplicablePricePort;
import com.retail.pricing.infrastructure.adapters.outbound.persistence.mapper.PricePersistenceMapper;
import com.retail.pricing.infrastructure.adapters.outbound.persistence.repository.JpaPriceRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class LoadApplicablePricePersistenceAdapter implements LoadApplicablePricePort {

    private final JpaPriceRepository jpaPriceRepository;
    private final PricePersistenceMapper pricePersistenceMapper;

    public LoadApplicablePricePersistenceAdapter(JpaPriceRepository jpaPriceRepository,
                                                 PricePersistenceMapper pricePersistenceMapper) {
        this.jpaPriceRepository = jpaPriceRepository;
        this.pricePersistenceMapper = pricePersistenceMapper;
    }

    @Override
    public Optional<Price> findApplicablePrice(ApplicablePriceCriteria criteria) {
        return jpaPriceRepository
                .findFirstByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByPriorityDesc(
                        criteria.brandId(),
                        criteria.productId(),
                        criteria.applicationDate(),
                        criteria.applicationDate()
                )
                .map(pricePersistenceMapper::toDomain);
    }
}
