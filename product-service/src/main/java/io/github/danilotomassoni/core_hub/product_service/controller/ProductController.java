package io.github.danilotomassoni.core_hub.product_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.github.danilotomassoni.core_hub.product_service.dto.request.ProductIDRequest;
import io.github.danilotomassoni.core_hub.product_service.dto.request.ProductRequest;
import io.github.danilotomassoni.core_hub.product_service.dto.response.ProductResponse;
import io.github.danilotomassoni.core_hub.product_service.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService service;

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody @Valid ProductRequest request) {
        ProductResponse response  = service.save(request);
        URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("{id}")
        .buildAndExpand(response.id().toString())
        .toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("{requestId}")
    public ResponseEntity<ProductResponse> findById(@PathVariable @Valid ProductIDRequest requestId) {
        return ResponseEntity.ok(service.findById(requestId.id()));
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @PutMapping("{requestId}")
    public ResponseEntity<Void> update(@PathVariable @Valid  ProductIDRequest requestId, @RequestBody ProductRequest request) {
        
        service.update(requestId.id(), request);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{requestId}")
    public ResponseEntity<Void> delete(@PathVariable @Valid  ProductIDRequest requestId) {
        
        service.delete(requestId.id());

        return ResponseEntity.noContent().build();
    }
    
}
