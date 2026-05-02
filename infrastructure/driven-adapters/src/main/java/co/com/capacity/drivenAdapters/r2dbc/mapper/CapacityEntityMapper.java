package co.com.capacity.drivenAdapters.r2dbc.mapper;

import co.com.capacity.drivenAdapters.r2dbc.entity.CapacityEntity;
import co.com.capacity.model.capacity.Capacity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CapacityEntityMapper {
    Capacity toModel(CapacityEntity entity);
    CapacityEntity toEntity(Capacity model);
}