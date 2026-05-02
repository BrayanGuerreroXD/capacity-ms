package co.com.capacity.mapper;

import co.com.capacity.dto.TechnologyCatalogResponse;
import co.com.capacity.model.technologycatalog.TechnologyCatalog;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TechnologyCatalogDTOMapper {
    TechnologyCatalogResponse toResponse(TechnologyCatalog technologyCatalog);
}