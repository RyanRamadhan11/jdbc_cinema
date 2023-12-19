package com.enigma.repository.impl;

import com.enigma.configuration.DbConnector;
import com.enigma.entity.Customer;
import com.enigma.entity.Rating;
import com.enigma.repository.RatingRepo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RatingRepoImpl implements RatingRepo {

    DbConnector dbConnector = new DbConnector();
    Connection conn = dbConnector.startConnect();


    @Override
    public List<Rating> getAll() {
        List<Rating> rating = new ArrayList<>();

        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM t_rating");
            ResultSet result = ps.executeQuery();

            while (result.next()) {
                Integer id = result.getInt("id");
                String code = result.getString("code");
                String description = result.getString("description");

                rating.add(new Rating(id, code, description));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return rating;
    }

    @Override
    public void addRating(Rating rating) {
        try {
            String query = "INSERT INTO t_rating (code, description) VALUES (?, ?)";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, rating.getCode());
            ps.setString(2, rating.getDescription());

            ps.executeUpdate();
            System.out.println("Rating added successfully");

        } catch (SQLException e) {
            System.out.println("Failed to add rating: " + e.getMessage());
        }
    }

    @Override
    public void updateRating(Rating rating) {
        try {
            String queryCheckId = "SELECT COUNT(*) FROM t_rating WHERE id = ?";
            String queryUpdate = "UPDATE t_rating SET code = ?, description = ? WHERE id = ?";

            PreparedStatement checkIdStatement = conn.prepareStatement(queryCheckId);
            checkIdStatement.setInt(1, rating.getId());

            try (ResultSet result = checkIdStatement.executeQuery()) {
                if (result.next() && result.getInt(1) > 0) {
                    try (PreparedStatement ps = conn.prepareStatement(queryUpdate)) {
                        ps.setString(1, rating.getCode());
                        ps.setString(2, rating.getDescription());
                        ps.setInt(3, rating.getId());

                        ps.executeUpdate();
                        System.out.println("Rating updated successfully");
                    }
                } else {
                    System.out.println("Rating not found with ID : " + rating.getId());
                }
            }
        }catch (SQLException e) {
            System.out.println("Failed to add rating: " + e.getMessage());
        }
    }

    @Override
    public void deleteRating(Integer id) {
        try {
            String queryCheckId = "SELECT COUNT(*) FROM t_rating WHERE id = ?";
            String queryDelete = "DELETE FROM t_rating WHERE id = ?";

            PreparedStatement checkIdStatement = conn.prepareStatement(queryCheckId);
            checkIdStatement.setInt(1, id);

            try (ResultSet result = checkIdStatement.executeQuery()) {
                if (result.next() && result.getInt(1) > 0) {
                    try (PreparedStatement ps = conn.prepareStatement(queryDelete)) {
                        ps.setInt(1, id);

                        ps.executeUpdate();
                        System.out.println("Rating deleted successfully");
                    }
                } else {
                    System.out.println("Rating not found with ID: " + id);
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to delete rating: " + e.getMessage());
        }
    }

    @Override
    public Rating getRatingById(Integer id) {
        Rating rating = new Rating();

        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM t_rating WHERE id = ?");
            ps.setInt(1, id);

            ResultSet result = ps.executeQuery();

            if (result.next()) {
                Integer id_rating = result.getInt("id");
                String code = result.getString("code");
                String description = result.getString("description");

                rating = new Rating(id_rating, code,description);

            }else {
                System.out.println("Rating not found with ID: " + id);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return rating;
    }
}
