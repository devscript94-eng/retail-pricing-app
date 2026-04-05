package com.retail.pricing.application.service;

import com.retail.pricing.domain.exception.PriceNotFoundException;
import com.retail.pricing.domain.model.Price;
import com.retail.pricing.domain.model.PriceRequest;
import com.retail.pricing.domain.repository.PriceRepository;
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
    private PriceRepository priceRepository;

    @InjectMocks
    private PriceService priceService;

    @Test
    void shouldReturnApplicablePriceWhenFound() {
        PriceRequest request = new PriceRequest(
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

        when(priceRepository.findApplicablePrice(request)).thenReturn(Optional.of(expected));

        Price result = priceService.execute(request);

        assertThat(result).isEqualTo(expected);
        verify(priceRepository).findApplicablePrice(request);
        verifyNoMoreInteractions(priceRepository);
    }

    @Test
    void shouldThrowPriceNotFoundExceptionWhenNoPriceExists() {
        PriceRequest request = new PriceRequest(
                LocalDateTime.of(2020, 6, 17, 10, 0),
                99999L,
                1L
        );

        when(priceRepository.findApplicablePrice(request)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> priceService.execute(request))
                .isInstanceOf(PriceNotFoundException.class)
                .hasMessageContaining("brandId=1")
                .hasMessageContaining("productId=99999");

        verify(priceRepository).findApplicablePrice(request);
        verifyNoMoreInteractions(priceRepository);
    }
}