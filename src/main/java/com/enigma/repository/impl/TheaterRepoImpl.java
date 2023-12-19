package com.enigma.repository.impl;

import com.enigma.configuration.DbConnector;
import com.enigma.entity.Film;
import com.enigma.entity.Theater;
import com.enigma.repository.TheaterRepo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TheaterRepoImpl implements TheaterRepo {
    DbConnector dbConnector = new DbConnector();
    Connection conn = dbConnector.startConnect();

    @Override
    public List<Theater> getAll() {
        List<Theater> theaters = new ArrayList<>();

        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM t_theater");
            ResultSet result = ps.executeQuery();

            while (result.next()) {
                Integer id = result.getInt("id");
                String theaterNumber = result.getString("theater_number");
                Integer stock = result.getInt("stock");
                Integer film_id = result.getInt("film_id");

                theaters.add(new Theater(id, theaterNumber, stock, film_id));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return theaters;
    }

    @Override
    public void addTheater(Theater theater) {
        try {
            String query = "INSERT INTO t_theater (theater_number, stock, film_id) VALUES (?, ?, ?)";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, theater.getTheaterNumber());
            ps.setInt(2, theater.getStock());
            ps.setInt(3, theater.getFilmId());

            ps.executeUpdate();
            System.out.println("Theater added successfully");
        } catch (SQLException e) {
            System.out.println("Failed to add theater: " + e.getMessage());
        }
    }

    @Override
    public void updateTheater(Theater theater) {
        try {
            String queryCheckId = "SELECT COUNT(*) FROM t_theater WHERE id = ?";
            String queryUpdate = "UPDATE t_theater SET theater_number = ?, stock = ?, film_id =? WHERE id = ?";

            PreparedStatement checkIdStatement = conn.prepareStatement(queryCheckId);
            checkIdStatement.setInt(1, theater.getId());

            try (ResultSet result = checkIdStatement.executeQuery()) {
                if (result.next() && result.getInt(1) > 0) {
                    try (PreparedStatement ps = conn.prepareStatement(queryUpdate)) {
                        ps.setString(1, theater.getTheaterNumber());
                        ps.setInt(2, theater.getStock());
                        ps.setInt(3, theater.getFilmId());
                        ps.setInt(4, theater.getId());

                        ps.executeUpdate();
                        System.out.println("Theater updated successfully");
                    }
                } else {
                    // ID tidak ditemukan
                    System.out.println("Theater not found with ID : " + theater.getId());
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to update theater: " + e.getMessage());
        }
    }

    @Override
    public void deleteTheater(Integer id) {
        try {
            String queryCheckId = "SELECT COUNT(*) FROM t_theater WHERE id = ?";
            String queryDelete = "DELETE FROM t_theater WHERE id = ?";

            PreparedStatement checkIdStatement = conn.prepareStatement(queryCheckId);
            checkIdStatement.setInt(1, id);

            try (ResultSet result = checkIdStatement.executeQuery()) {
                if (result.next() && result.getInt(1) > 0) {
                    try (PreparedStatement ps = conn.prepareStatement(queryDelete)) {
                        ps.setInt(1, id);
                        ps.executeUpdate();
                        System.out.println("Theater deleted successfully");
                    }
                } else {
                    System.out.println("Theater not found with ID: " + id);
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to delete theater: " + e.getMessage());
        }
    }

    @Override
    public Theater getTheaterById(Integer id) {
        Theater theater = new Theater();

        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM t_theater WHERE id = ?");
            ps.setInt(1, id);

            ResultSet result = ps.executeQuery();

            if (result.next()) {
                Integer id_theater = result.getInt("id");
                String theaterNumber = result.getString("theater_number");
                Integer stock = result.getInt("stock");
                Integer film_id = result.getInt("film_id");

                theater = new Theater(id_theater, theaterNumber, stock, film_id);

            } else {
                System.out.println("Theater not found with ID: " + id);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return theater;
    }
}
