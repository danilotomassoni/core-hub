package io.github.danilotomassoni.core_hub.product_service.dto.request;

import org.hibernate.validator.constraints.UUID;

import jakarta.validation.constraints.NotBlank;

public record ProductIDRequest(@NotBlank @UUID String id) {

}
