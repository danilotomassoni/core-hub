package io.github.danilotomassoni.core_hub.user_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.github.danilotomassoni.core_hub.user_service.dto.request.RoleRequest;
import io.github.danilotomassoni.core_hub.user_service.dto.request.UserRequest;
import io.github.danilotomassoni.core_hub.user_service.dto.response.UserResponse;

public interface UserService {

    UserResponse save(UserRequest request);


    UserResponse findById(String id);

    Page<UserResponse> findAll(Pageable pageable);

    UserResponse update(String id, UserRequest userRequest);

    void delete(String id);

    UserResponse updateRole(String id, RoleRequest request);
}
