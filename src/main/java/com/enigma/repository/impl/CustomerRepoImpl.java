package com.enigma.repository.impl;

import com.enigma.configuration.DbConnector;
import com.enigma.entity.Customer;
import com.enigma.repository.CustomerRepo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerRepoImpl implements CustomerRepo {
    DbConnector dbConnector = new DbConnector();
    Connection conn = dbConnector.startConnect();

    @Override
    public List<Customer> getAll() {
        List<Customer> customers = new ArrayList<>();

        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM m_customer");
            ResultSet result = ps.executeQuery();

            while (result.next()) {
                Integer id = result.getInt("id");
                String name = result.getString("name");
                String birthDate = result.getString("birth_date");

                customers.add(new Customer(id, name, birthDate));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return customers;
    }

    @Override
    public void addCustomer(Customer customer) {

        try {
            String query = "INSERT INTO m_customer (name, birth_date) VALUES (?, ?)";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, customer.getName());

            java.sql.Date sqlDate = java.sql.Date.valueOf(customer.getBirthDate());
            ps.setDate(2, sqlDate);

            ps.executeUpdate();
            System.out.println("Customer added successfully");
        } catch (SQLException e) {
            System.out.println("Failed to add customer: " + e.getMessage());
        }
    }

    @Override
    public void updateCustomer(Customer customer) {
        try {
            String queryCheckId = "SELECT COUNT(*) FROM m_customer WHERE id = ?";
            String queryUpdate = "UPDATE m_customer SET name = ?, birth_date = ? WHERE id = ?";

            PreparedStatement checkIdStatement = conn.prepareStatement(queryCheckId);
            checkIdStatement.setInt(1, customer.getId());

            try (ResultSet result = checkIdStatement.executeQuery()) {
                if (result.next() && result.getInt(1) > 0) {
                    // ID ditemukan lakukan update
                    try (PreparedStatement ps = conn.prepareStatement(queryUpdate)) {
                        ps.setString(1, customer.getName());

                        java.sql.Date sqlDate = java.sql.Date.valueOf(customer.getBirthDate());
                        ps.setDate(2, sqlDate);

                        ps.setInt(3, customer.getId());

                        ps.executeUpdate();
                        System.out.println("Customer updated successfully");
                    }
                } else {
                    System.out.println("Customer not found with ID : " + customer.getId());
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to update customer: " + e.getMessage());
        }
    }


    @Override
    public void deleteCustomer(Integer id) {
        try {
            String queryCheckId = "SELECT COUNT(*) FROM m_customer WHERE id = ?";
            String queryDelete = "DELETE FROM m_customer WHERE id = ?";

            PreparedStatement checkIdStatement = conn.prepareStatement(queryCheckId);
            checkIdStatement.setInt(1, id);

            try (ResultSet result = checkIdStatement.executeQuery()) {
                if (result.next() && result.getInt(1) > 0) {
                    try (PreparedStatement ps = conn.prepareStatement(queryDelete)) {
                        ps.setInt(1, id);

                        ps.executeUpdate();
                        System.out.println("Customer deleted successfully");
                    }
                } else {
                    System.out.println("Customer not found with ID: " + id);
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to delete customer: " + e.getMessage());
        }
    }


    @Override
    public Customer getCustomerById(Integer id) {
        Customer customers = new Customer();

        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM m_customer WHERE id = ?");
            ps.setInt(1, id);

            ResultSet result = ps.executeQuery();

            if (result.next()) {
                Integer id_customer = result.getInt("id");
                String name = result.getString("name");
                String birthDate = result.getString("birth_date");

                customers = new Customer(id_customer, name, birthDate);

            }else {
                System.out.println("Customer not found with ID: " + id);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return customers;
    }

}
