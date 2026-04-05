package com.retail.pricing.infrastructure.adapters.outbound.persistence.repository;

import com.retail.pricing.infrastructure.adapters.outbound.persistence.entity.PriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface JpaPriceRepository extends JpaRepository<PriceEntity, Long> {

    Optional<PriceEntity> findFirstByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByPriorityDesc(
            Long brandId,
            Long productId,
            LocalDateTime applicationDate1,
            LocalDateTime applicationDate2
    );
}