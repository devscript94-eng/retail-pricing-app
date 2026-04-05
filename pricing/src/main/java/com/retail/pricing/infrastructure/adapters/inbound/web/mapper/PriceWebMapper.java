package com.retail.pricing.infrastructure.adapters.inbound.web.mapper;

import com.retail.pricing.domain.model.Price;
import com.retail.pricing.infrastructure.adapters.inbound.web.dto.PriceResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PriceWebMapper {

    @Mapping(source = "amount", target = "finalPrice")
    PriceResponseDTO toDto(Price price);
}