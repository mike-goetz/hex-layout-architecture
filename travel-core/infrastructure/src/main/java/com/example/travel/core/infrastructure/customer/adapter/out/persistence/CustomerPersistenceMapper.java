package com.example.travel.core.infrastructure.customer.adapter.out.persistence;

import com.example.travel.core.domain.customer.model.CustomerProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CustomerPersistenceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    CustomerProfileJpaEntity toJpaEntity(CustomerProfile domain);

    CustomerProfile toDomain(CustomerProfileJpaEntity entity);
}