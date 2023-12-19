package com.enigma.repository;

import com.enigma.entity.Customer;

import java.util.List;

public interface CustomerRepo {

    List<Customer> getAll();
    void addCustomer(Customer customer);
    void updateCustomer(Customer customer);
    void deleteCustomer(Integer id);
    Customer getCustomerById(Integer id);
}
