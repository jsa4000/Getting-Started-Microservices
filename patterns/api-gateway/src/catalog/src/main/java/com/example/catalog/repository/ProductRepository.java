package com.example.catalog.repository;

import com.example.catalog.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.stream.Stream;

public interface ProductRepository extends PagingAndSortingRepository<Product, String> {
    List<Product> findByName(String name);
    Page<Product> findByBrand(String brand, Pageable pageable);
}

