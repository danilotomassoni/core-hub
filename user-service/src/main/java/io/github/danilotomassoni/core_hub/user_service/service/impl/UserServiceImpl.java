package io.github.danilotomassoni.core_hub.user_service.service.impl;

import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import io.github.danilotomassoni.core_hub.user_service.dto.request.RoleRequest;
import io.github.danilotomassoni.core_hub.user_service.dto.request.UserRequest;
import io.github.danilotomassoni.core_hub.user_service.dto.response.UserResponse;
import io.github.danilotomassoni.core_hub.user_service.entity.RoleType;
import io.github.danilotomassoni.core_hub.user_service.entity.User;
import io.github.danilotomassoni.core_hub.user_service.mapper.UserMapper;
import io.github.danilotomassoni.core_hub.user_service.messaging.RabbitProducer;
import io.github.danilotomassoni.core_hub.user_service.messaging.UserRoleEvent;
import io.github.danilotomassoni.core_hub.user_service.repository.UserRepository;
import io.github.danilotomassoni.core_hub.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper mapper;
    private final RabbitProducer producer;

    @Override
    public UserResponse save(UserRequest request) {
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setRole(RoleType.USER);

        UserResponse response = mapper.toDto(repository.save(user));

        log.info("User registered successfully. ID: {}", response.id());
        return response;
    }

    @Override
    public UserResponse findById(String id) {
        User user = repository.findById(UUID.fromString(id))
                .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + id));

        log.info("User retrieved successfully. ID: {}", user.getId());
        return mapper.toDto(user);
    }


    @Override
    public Page<UserResponse> findAll(Pageable pageable) {
        log.info("Retrieving paginated users list. Page: {}, Size: {}", pageable.getPageNumber(),
                pageable.getPageSize());
        return repository.findAll(pageable).map(mapper::toDto);
    }

    @Override
    public UserResponse update(String id, UserRequest userRequest) {
        User user = repository.findById(UUID.fromString(id))
                .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + id));

        user.setUsername(userRequest.username());
        user.setEmail(userRequest.email());

        UserResponse response = mapper.toDto(repository.save(user));
        log.info("User updated successfully. ID: {}", user.getId());
        return response;
    }

    @Override
    public void delete(String id) {
        User user = repository.findById(UUID.fromString(id))
                .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + id));

        repository.delete(user);
        log.info("User deleted successfully. ID: {}", user.getId());
    }

    @Override
    public UserResponse updateRole(String id, RoleRequest request) {
        User user = repository.findById(UUID.fromString(id))
                .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + id));
        
        user.setRole(request.role());
        producer.sendUserRoleUpdatedEvent(new UserRoleEvent(user.getId(),user.getRole()));
        UserResponse response = mapper.toDto(repository.save(user));
        return response;
    }


    

}
