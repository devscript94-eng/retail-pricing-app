package com.retail.pricing.infrastructure.adapters.outbound.persistence.mapper;

import com.retail.pricing.domain.model.Price;
import com.retail.pricing.infrastructure.adapters.outbound.persistence.entity.PriceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PricePersistenceMapper {

    @Mapping(source = "price", target = "amount")
    @Mapping(source = "currency", target = "currency")
    Price toDomain(PriceEntity entity);
}
