package co.com.capacity.mapper;

import co.com.capacity.dto.CapacityRequest;
import co.com.capacity.dto.CapacityResponse;
import co.com.capacity.model.capacity.Capacity;
import co.com.capacity.dto.CapacityTechnologyRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CapacityDTOMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "technologyIds", ignore = true)
    Capacity toModel(CapacityRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "technologyIds", source = "technologies")
    Capacity toModelWithTechnologies(CapacityRequest request);

    default List<Long> map(List<CapacityTechnologyRequest> technologies) {
        if (technologies == null) {
            return null;
        }
        return technologies.stream()
                .map(CapacityTechnologyRequest::getTechnologyId)
                .collect(Collectors.toList());
    }

    CapacityResponse toResponse(Capacity capacity);
}