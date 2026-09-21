package io.github.danilotomassoni.core_hub.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import io.github.danilotomassoni.core_hub.user_service.dto.request.UserRequest;
import io.github.danilotomassoni.core_hub.user_service.dto.response.UserResponse;
import io.github.danilotomassoni.core_hub.user_service.entity.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    User toEntity(UserRequest request);
    UserResponse toDto(User user);
}
