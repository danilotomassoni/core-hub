package io.github.danilotomassoni.core_hub.product_service.service.impl;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import io.github.danilotomassoni.core_hub.product_service.dto.request.ProductRequest;
import io.github.danilotomassoni.core_hub.product_service.dto.response.ProductResponse;
import io.github.danilotomassoni.core_hub.product_service.entity.Product;
import io.github.danilotomassoni.core_hub.product_service.mapper.ProductMapper;
import io.github.danilotomassoni.core_hub.product_service.repository.ProductRepository;
import io.github.danilotomassoni.core_hub.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{

    private final ProductRepository repository;
    private final ProductMapper mapper;

    @Override
    public ProductResponse save(ProductRequest request) {
        Product product = repository.save(mapper.toRequestEntity(request));
        log.info("CREATED 201 (POST) ID {}",product.getId());
        return mapper.toResponse(product);
    }

    @Override
    public ProductResponse findById(String id) {
        Product product = repository.findById(UUID.fromString(id)).orElseThrow();
        log.info("OK 200 (GET) ID {}",product.getId());
        return mapper.toResponse(product);
    }

    @Override
    public Page<ProductResponse> findAll(Pageable pageable) {

        Page<ProductResponse> page = repository.findAll(pageable).map(p -> mapper.toResponse(p));

        log.info("OK 200 (GET) SIZE {}", page.getTotalElements());
        return page;
    }

    @Override
    public ProductResponse update(String id, ProductRequest request) {

        ProductResponse response = findById(id);
        Product product = mapper.toRequestEntity(request);
        product.setId(response.id());

        repository.saveAndFlush(product);
        log.info("OK 200 (PUT) ID {}", product.getId());
        return response;
    }

    @Override
    public void delete(String id) {
        repository.findById(UUID.fromString(id)).orElseThrow();
        repository.deleteById(UUID.fromString(id));
        log.info("NOT CONTENT 204 (DELETE) ID {}", id);
    }


}
