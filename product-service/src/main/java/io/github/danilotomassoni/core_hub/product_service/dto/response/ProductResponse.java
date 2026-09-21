package io.github.danilotomassoni.core_hub.product_service.dto.response;

import java.util.UUID;

public record ProductResponse(
    UUID id,
    String title,
    String description,
    Double price,
    Integer quantity
) {

}
