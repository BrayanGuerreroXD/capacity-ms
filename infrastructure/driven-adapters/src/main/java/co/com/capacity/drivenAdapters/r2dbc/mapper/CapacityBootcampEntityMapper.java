package co.com.capacity.drivenAdapters.r2dbc.mapper;

import co.com.capacity.drivenAdapters.r2dbc.entity.CapacityBootcampEntity;
import co.com.capacity.model.capacitybootcamp.CapacityBootcamp;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CapacityBootcampEntityMapper {
    CapacityBootcamp toModel(CapacityBootcampEntity entity);
    CapacityBootcampEntity toEntity(CapacityBootcamp model);
}