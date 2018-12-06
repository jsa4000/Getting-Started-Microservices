package com.example.catalog.service;

import com.example.catalog.model.Product;
import com.example.catalog.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository repository;

    @Override
    public Page<Product> findAll(Pageable pageable) { return repository.findAll(pageable); }

    @Override
    public Product create(Product product) {return  repository.save(product); }

    @Override
    public void delete(String id) {repository.deleteById(id);}

    @Override
    public Optional<Product> getProductById(String id) {return repository.findById(id); }

}
