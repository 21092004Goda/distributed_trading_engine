package org.kuro.service.mapper;

import org.kuro.model.dto.request.web.user.UserCreateRequest;
import org.kuro.model.dto.response.web.user.UserCreateResponse;
import org.kuro.model.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    // CreateRequest -> Entity
    @Mapping(target = "username", source = "username")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "passwordHash", source = "passwordHash")
    @Mapping(target = "status", expression = "java(request.getStatus().name())")
    UserEntity toEntity(UserCreateRequest request);

    // Entity -> Response
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "timestamp", source = "updatedAt")
    UserCreateResponse toResponse(UserEntity entity);

}
