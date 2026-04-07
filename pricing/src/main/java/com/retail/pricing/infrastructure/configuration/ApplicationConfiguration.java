package com.retail.pricing.infrastructure.configuration;

import com.retail.pricing.application.port.in.GetPriceUseCase;
import com.retail.pricing.application.port.out.LoadApplicablePricePort;
import com.retail.pricing.application.service.PriceService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

    @Bean
    GetPriceUseCase getPriceUseCase(LoadApplicablePricePort loadApplicablePricePort) {
        return new PriceService(loadApplicablePricePort);
    }
}
