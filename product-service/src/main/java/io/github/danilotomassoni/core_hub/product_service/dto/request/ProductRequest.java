package io.github.danilotomassoni.core_hub.product_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductRequest(
        @NotBlank String title,
        @NotBlank String description,
        @NotNull Double price,
        @NotNull Integer quantity) {

}
