package io.github.danilotomassoni.core_hub.product_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.github.danilotomassoni.core_hub.product_service.dto.request.ProductRequest;
import io.github.danilotomassoni.core_hub.product_service.dto.response.ProductResponse;

public interface ProductService {

    ProductResponse save(ProductRequest request);

    ProductResponse findById(String id);

    Page<ProductResponse> findAll(Pageable pageable);
    
    ProductResponse update(String id, ProductRequest request);

    void delete(String id);
}
