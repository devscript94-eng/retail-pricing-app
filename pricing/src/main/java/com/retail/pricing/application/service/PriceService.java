package com.retail.pricing.application.service;

import com.retail.pricing.application.port.GetPriceUseCase;
import com.retail.pricing.domain.exception.PriceNotFoundException;
import com.retail.pricing.domain.model.Price;
import com.retail.pricing.domain.model.PriceRequest;
import com.retail.pricing.domain.repository.PriceRepository;
import org.springframework.stereotype.Service;


@Service
public class PriceService implements GetPriceUseCase {

    private final PriceRepository priceRepository;

    public PriceService(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    @Override
    public Price execute(PriceRequest request) {
        return priceRepository.findApplicablePrice(request)
                .orElseThrow(() -> new PriceNotFoundException(
                        request.brandId(),
                        request.productId(),
                        request.applicationDate()
                ));
    }
}