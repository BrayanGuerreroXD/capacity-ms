package co.com.capacity.drivenAdapters.r2dbc.mapper;

import co.com.capacity.drivenAdapters.r2dbc.entity.TechnologyCatalogEntity;
import co.com.capacity.model.technologycatalog.TechnologyCatalog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TechnologyCatalogEntityMapper {
    TechnologyCatalog toModel(TechnologyCatalogEntity entity);
    TechnologyCatalogEntity toEntity(TechnologyCatalog model);
}