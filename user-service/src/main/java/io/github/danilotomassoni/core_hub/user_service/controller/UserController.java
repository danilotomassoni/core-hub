package io.github.danilotomassoni.core_hub.user_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.danilotomassoni.core_hub.user_service.dto.request.RoleRequest;
import io.github.danilotomassoni.core_hub.user_service.dto.request.UserRequest;
import io.github.danilotomassoni.core_hub.user_service.dto.response.UserResponse;
import io.github.danilotomassoni.core_hub.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping
    public ResponseEntity<UserResponse> save(@RequestBody @Valid UserRequest request) {
        log.info("Receiving request to register user with email: {}", request.email());
        UserResponse response = service.save(request);

        // Boa prática: Retornar o corpo criado e o cabeçalho Location do recurso
        URI location = URI.create("/users/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(
            @RequestHeader(value = "X-User-Sub", required = false) String sub,
            @RequestHeader(value = "X-User-Email", required = false) String email) {

        log.info("Receiving request to fetch current user context. Sub: {}, Email: {}", sub, email);

        if (sub != null && !sub.isBlank()) {
            return ResponseEntity.ok(service.findById(sub));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable("id") String id) {
        log.info("Receiving request to fetch user by ID: {}", id);
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    public ResponseEntity<Page<UserResponse>> findAll(Pageable pageable) {
        log.info("Receiving request to fetch all users with pagination");
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable("id") String id,
            @RequestBody @Valid UserRequest userRequest) {
        log.info("Receiving request to update user with ID: {}", id);
        return ResponseEntity.ok(service.update(id, userRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        log.info("Receiving request to delete user with ID: {}", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<UserResponse> updateRole(
            @PathVariable("id") String id,
            @RequestBody @Valid RoleRequest request) {
        log.info("Receiving request from ADMIN to update role of user ID: {} to {}", id, request.role());
        return ResponseEntity.ok(service.updateRole(id, request));
    }
}
