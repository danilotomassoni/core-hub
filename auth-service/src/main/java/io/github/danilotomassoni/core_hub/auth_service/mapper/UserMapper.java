package io.github.danilotomassoni.core_hub.auth_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import io.github.danilotomassoni.core_hub.auth_service.dto.request.RegisterRequest;
import io.github.danilotomassoni.core_hub.auth_service.entity.User;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    User toEntity(RegisterRequest request);
}
