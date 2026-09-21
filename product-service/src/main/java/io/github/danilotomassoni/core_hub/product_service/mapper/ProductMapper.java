package io.github.danilotomassoni.core_hub.product_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import io.github.danilotomassoni.core_hub.product_service.dto.request.ProductRequest;
import io.github.danilotomassoni.core_hub.product_service.dto.response.ProductResponse;
import io.github.danilotomassoni.core_hub.product_service.entity.Product;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    ProductResponse toResponse(Product product);
    ProductRequest toRequest(Product product);
    Product toRequestEntity(ProductRequest request);
    Product toResponseEntity(ProductResponse request);
}
