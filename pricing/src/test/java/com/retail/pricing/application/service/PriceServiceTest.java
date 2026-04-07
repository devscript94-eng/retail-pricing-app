package com.retail.pricing.application.service;

import com.retail.pricing.application.exception.PriceNotFoundException;
import com.retail.pricing.application.model.ApplicablePriceCriteria;
import com.retail.pricing.domain.model.Price;
import com.retail.pricing.application.port.in.GetPriceQuery;
import com.retail.pricing.application.port.out.LoadApplicablePricePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

    @Mock
    private LoadApplicablePricePort loadApplicablePricePort;

    @InjectMocks
    private PriceService priceService;

    @Test
    void shouldReturnApplicablePriceWhenFound() {
        GetPriceQuery request = new GetPriceQuery(
                LocalDateTime.of(2020, 6, 14, 16, 0),
                35455L,
                1L
        );

        Price expected = new Price(
                1L,
                LocalDateTime.of(2020, 6, 14, 15, 0),
                LocalDateTime.of(2020, 6, 14, 18, 30),
                2,
                35455L,
                1,
                new BigDecimal("25.45"),
                "EUR"
        );

        ApplicablePriceCriteria criteria = ApplicablePriceCriteria.from(request);

        when(loadApplicablePricePort.findApplicablePrice(criteria)).thenReturn(Optional.of(expected));

        Price result = priceService.execute(request);

        assertThat(result).isEqualTo(expected);
        verify(loadApplicablePricePort).findApplicablePrice(criteria);
        verifyNoMoreInteractions(loadApplicablePricePort);
    }

    @Test
    void shouldThrowPriceNotFoundExceptionWhenNoPriceExists() {
        GetPriceQuery request = new GetPriceQuery(
                LocalDateTime.of(2020, 6, 17, 10, 0),
                99999L,
                1L
        );

        ApplicablePriceCriteria criteria = ApplicablePriceCriteria.from(request);

        when(loadApplicablePricePort.findApplicablePrice(criteria)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> priceService.execute(request))
                .isInstanceOf(PriceNotFoundException.class)
                .hasMessageContaining("brandId=1")
                .hasMessageContaining("productId=99999");

        verify(loadApplicablePricePort).findApplicablePrice(criteria);
        verifyNoMoreInteractions(loadApplicablePricePort);
    }
}
