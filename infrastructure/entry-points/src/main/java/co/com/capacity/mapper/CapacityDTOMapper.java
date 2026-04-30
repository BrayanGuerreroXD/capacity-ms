package co.com.capacity.mapper;

import co.com.capacity.dto.CapacityRequest;
import co.com.capacity.dto.CapacityResponse;
import co.com.capacity.model.capacity.Capacity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CapacityDTOMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Capacity toModel(CapacityRequest request);

    CapacityResponse toResponse(Capacity capacity);
}