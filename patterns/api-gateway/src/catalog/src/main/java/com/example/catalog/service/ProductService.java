package com.example.catalog.service;

import com.example.catalog.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProductService {
    Page<Product> findAll(Pageable pageable);

    Product create(Product product);

    void delete(String id);

    Optional<Product> getProductById(String id);
}
