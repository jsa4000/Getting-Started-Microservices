package com.example.catalog.repository;

import com.example.catalog.model.Product;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static junit.framework.TestCase.assertTrue;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductRepositoryTests {

    @Autowired ProductRepository repository;

    @Test
    public void createProduct_valid_shouldReturnOk() {
        Product expectedProduct = getDefaultProduct();

        Product actualProduct = repository.save(expectedProduct);

        assertTrue(actualProduct.getBrand().equals(expectedProduct.getBrand()));
        assertTrue(actualProduct.getDescription().equals(expectedProduct.getDescription()));
        assertTrue(actualProduct.getModel().equals(expectedProduct.getModel()));
        assertTrue(actualProduct.getName().equals(expectedProduct.getName()));
        assertNull(actualProduct.getImageUrl());

        repository.deleteById(actualProduct.getId());
    }

    @Test
    public void findByName_validProductName_shouldReturnOk() {
        Product expectedProduct = getDefaultProduct();
        expectedProduct = repository.save(expectedProduct);

        List<Product> actualProduct = repository.findByName(expectedProduct.getName());
        assertTrue(actualProduct.stream().findAny().isPresent());

        repository.deleteById(expectedProduct.getId());
    }

    private Product getDefaultProduct() {
        return new Product(null,"Raspberri Pi","Mini board, 4 quad-core, 1Gb, WiFi, 4 USB",
                "3B+", "Raspberry Pi Foundation", null);
    }
}
