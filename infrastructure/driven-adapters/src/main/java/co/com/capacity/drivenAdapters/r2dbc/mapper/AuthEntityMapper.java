package co.com.capacity.drivenAdapters.r2dbc.mapper;

import co.com.capacity.drivenAdapters.r2dbc.entity.AuthEntity;
import co.com.capacity.model.auth.Auth;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AuthEntityMapper {
    Auth toModel(AuthEntity entity);

    @Mapping(target = "id", ignore = true)
    AuthEntity toEntity(Auth model);
}