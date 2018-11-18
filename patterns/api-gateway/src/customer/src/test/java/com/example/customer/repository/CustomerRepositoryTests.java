package com.example.customer.repository;

import com.example.customer.model.Address;
import com.example.customer.model.Customer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static junit.framework.TestCase.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CustomerRepositoryTests {

    @Autowired CustomerRepository repository;

    @Test
    public void createCustomer_valid_shouldReturnOk() {
        Customer expectedCustomer = getDefaultCustomer();

        Customer actualCustomer = repository.save(expectedCustomer);

        assertTrue(actualCustomer.getEmail().equals(expectedCustomer.getEmail()));
        assertTrue(actualCustomer.getLastName().equals(expectedCustomer.getLastName()));
        assertTrue(actualCustomer.getFirstName().equals(expectedCustomer.getFirstName()));
        assertTrue(actualCustomer.getAddress().getStreetAddress()
                .equals(expectedCustomer.getAddress().getStreetAddress()));

        repository.deleteById(actualCustomer.getId());
    }

    @Test
    public void findByLastName_validCustomerName_shouldReturnOk() {
        Customer expectedCustomer = getDefaultCustomer();
        expectedCustomer = repository.save(expectedCustomer);

        List<Customer> actualCustomer = repository.findByLastName(expectedCustomer.getLastName());
        assertTrue(actualCustomer.stream().findAny().isPresent());

        repository.deleteById(expectedCustomer.getId());
    }

    private Customer getDefaultCustomer() {
        return new Customer(null,"Javier","Santos","jsa4000@gmail.com",
                new Address(1,"Calle GranVia",
                        "Madrid","Madrid","28034","Spain"));
    }
}
