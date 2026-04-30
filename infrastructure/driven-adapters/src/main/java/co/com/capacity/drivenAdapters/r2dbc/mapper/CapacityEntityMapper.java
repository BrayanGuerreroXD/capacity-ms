package co.com.capacity.drivenAdapters.r2dbc.mapper;

import co.com.capacity.drivenAdapters.r2dbc.entity.CapacityEntity;
import co.com.capacity.model.capacity.Capacity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CapacityEntityMapper {
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Capacity toModel(CapacityEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CapacityEntity toEntity(Capacity model);
}