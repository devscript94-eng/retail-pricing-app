package com.retail.pricing.infrastructure.adapters.outbound.persistence.repository;

import com.retail.pricing.infrastructure.adapters.outbound.persistence.entity.PriceEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.sql.init.mode=always"
})
class JpaPriceRepositoryIntegrationTest {

    @Autowired
    private JpaPriceRepository jpaPriceRepository;

    @Test
    void shouldReturnPriceList2At2020_06_14_16_00() {
        Optional<PriceEntity> result =
                jpaPriceRepository.findFirstByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByPriorityDesc(
                        1L,
                        35455L,
                        LocalDateTime.of(2020, 6, 14, 16, 0),
                        LocalDateTime.of(2020, 6, 14, 16, 0)
                );

        assertThat(result).isPresent();
        assertThat(result.get().getPriceList()).isEqualTo(2);
        assertThat(result.get().getPrice()).isEqualByComparingTo("25.45");
        assertThat(result.get().getCurr()).isEqualTo("EUR");
    }

    @Test
    void shouldReturnPriceList1At2020_06_14_21_00() {
        Optional<PriceEntity> result =
                jpaPriceRepository.findFirstByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByPriorityDesc(
                        1L,
                        35455L,
                        LocalDateTime.of(2020, 6, 14, 21, 0),
                        LocalDateTime.of(2020, 6, 14, 21, 0)
                );

        assertThat(result).isPresent();
        assertThat(result.get().getPriceList()).isEqualTo(1);
        assertThat(result.get().getPrice()).isEqualByComparingTo("35.50");
    }

    @Test
    void shouldReturnEmptyWhenNoApplicablePriceExists() {
        Optional<PriceEntity> result =
                jpaPriceRepository.findFirstByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByPriorityDesc(
                        1L,
                        99999L,
                        LocalDateTime.of(2020, 6, 14, 21, 0),
                        LocalDateTime.of(2020, 6, 14, 21, 0)
                );

        assertThat(result).isEmpty();
    }
}