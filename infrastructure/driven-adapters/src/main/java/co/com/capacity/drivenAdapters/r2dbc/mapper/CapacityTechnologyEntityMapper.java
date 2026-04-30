package co.com.capacity.drivenAdapters.r2dbc.mapper;

import co.com.capacity.drivenAdapters.r2dbc.entity.CapacityTechnologyEntity;
import co.com.capacity.model.capacitytechnology.CapacityTechnology;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CapacityTechnologyEntityMapper {
    CapacityTechnology toModel(CapacityTechnologyEntity entity);
    CapacityTechnologyEntity toEntity(CapacityTechnology model);
}