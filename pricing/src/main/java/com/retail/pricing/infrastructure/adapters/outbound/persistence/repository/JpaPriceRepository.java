package com.retail.pricing.infrastructure.adapters.outbound.persistence.repository;

import com.retail.pricing.infrastructure.adapters.outbound.persistence.entity.PriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface JpaPriceRepository extends JpaRepository<PriceEntity, Long> {

    @Query("""
            SELECT price
            FROM PriceEntity price
            WHERE price.brandId = :brandId
              AND price.productId = :productId
              AND price.startDate <= :applicationDate
              AND price.endDate >= :applicationDate
            ORDER BY price.priority DESC
            LIMIT 1
            """)
    Optional<PriceEntity> findApplicablePrice(
            @Param("brandId") Long brandId,
            @Param("productId") Long productId,
            @Param("applicationDate") LocalDateTime applicationDate
    );
}