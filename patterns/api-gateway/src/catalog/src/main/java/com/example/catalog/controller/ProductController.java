package com.example.catalog.controller;

import com.example.catalog.exceptions.ProductNotFoundException;
import com.example.catalog.model.Product;
import com.example.catalog.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Api(tags="Products", description = "Products View.")
@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired private ProductService service;

    @ApiOperation(value = "Get a pageable list of products")
    @GetMapping("/")
    ResponseEntity<PagedResources<Product>> getProducts(Pageable pageable, PagedResourcesAssembler assembler) {
        Page<Product> products = service.findAll(pageable);
        return new ResponseEntity<>(assembler.toResource(products), HttpStatus.OK);
    }

    @ApiOperation(value = "Get a product by Id")
    @GetMapping("/{id}")
    ResponseEntity<Product> getProductById(@PathVariable String id) {
        Optional<Product> product =  service.getProductById(id);
        if (product.isPresent())
            return new ResponseEntity<>(product.get(), HttpStatus.OK);
        else
           throw new ProductNotFoundException("Resource not found in the database");
    }

    @ApiOperation(value = "Create a new product")
    @PostMapping("/")
    ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product createdProduct =  service.create(product);
        return new ResponseEntity<>(createdProduct, HttpStatus.OK);
    }

    @ApiOperation(value = "Delete an existing product")
    @DeleteMapping("/{id}")
    ResponseEntity deleteProduct(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.ok(StringUtils.EMPTY);
    }
}

