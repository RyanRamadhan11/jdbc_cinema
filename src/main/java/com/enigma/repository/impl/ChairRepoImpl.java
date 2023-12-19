package com.enigma.repository.impl;

import com.enigma.configuration.DbConnector;
import com.enigma.entity.Chair;
import com.enigma.entity.Theater;
import com.enigma.repository.ChairRepo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChairRepoImpl implements ChairRepo {
    DbConnector dbConnector = new DbConnector();
    Connection conn = dbConnector.startConnect();

    @Override
    public List<Chair> getAll() {
        List<Chair> chairs = new ArrayList<>();

        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM t_seat");
            ResultSet result = ps.executeQuery();

            while (result.next()) {
                Integer id = result.getInt("id");
                String seatNumber = result.getString("seat_number");
                Integer theaterId = result.getInt("theater_id");

                chairs.add(new Chair(id, seatNumber, theaterId));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return chairs;
    }

    @Override
    public void addChair(Chair chair) {
        try {
            conn.setAutoCommit(false);

            String chairQuery = "INSERT INTO t_seat (seat_number, theater_id) VALUES (?, ?)";
            try (PreparedStatement chairStatement = conn.prepareStatement(chairQuery)) {
                chairStatement.setString(1, chair.getSeat_number());
                chairStatement.setInt(2, chair.getTheaterId());

                chairStatement.executeUpdate();
                System.out.println("Chair/Seat added successfully");
            }

            String updateStockQuery = "UPDATE t_theater SET stock = stock + 1 WHERE id = ?";
            try (PreparedStatement updateStockStatement = conn.prepareStatement(updateStockQuery)) {
                updateStockStatement.setInt(1, chair.getTheaterId());
                updateStockStatement.executeUpdate();
                System.out.println("Theater stock updated successfully");
            }

            conn.commit();

        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                System.out.println("Failed to rollback transaction: " + ex.getMessage());
            }
            System.out.println("Transaction failed: " + e.getMessage());
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                System.out.println("Failed to set auto-commit: " + ex.getMessage());
            }
        }
    }

    @Override
    public void updateChair(Chair chair) {
        try {
            conn.setAutoCommit(false);

            int currentTheaterId;

            String getCurrentTheaterQuery = "SELECT theater_id FROM t_seat WHERE id = ?";
            try (PreparedStatement getCurrentTheaterStatement = conn.prepareStatement(getCurrentTheaterQuery)) {
                getCurrentTheaterStatement.setInt(1, chair.getId());
                try (ResultSet resultSet = getCurrentTheaterStatement.executeQuery()) {
                    if (resultSet.next()) {
                        currentTheaterId = resultSet.getInt("theater_id");
                    } else {
                        throw new SQLException("Chair/Seat not found with ID: " + chair.getId());
                    }
                }
            }

            String queryCheckId = "SELECT COUNT(*) FROM t_seat WHERE id = ?";
            try (PreparedStatement checkIdStatement = conn.prepareStatement(queryCheckId)) {
                checkIdStatement.setInt(1, chair.getId());

                try (ResultSet result = checkIdStatement.executeQuery()) {
                    if (result.next() && result.getInt(1) > 0) {

                        String queryUpdate = "UPDATE t_seat SET seat_number = ?, theater_id = ? WHERE id = ?";
                        try (PreparedStatement updateChairStatement = conn.prepareStatement(queryUpdate)) {
                            updateChairStatement.setString(1, chair.getSeat_number());
                            updateChairStatement.setInt(2, chair.getTheaterId());
                            updateChairStatement.setInt(3, chair.getId());

                            updateChairStatement.executeUpdate();
                            System.out.println("Chair/Seat updated successfully");
                        }

                        // Mengurangkan stok teater lama
                        String decreaseStockQuery = "UPDATE t_theater SET stock = stock - 1 WHERE id = ?";
                        try (PreparedStatement decreaseStockStatement = conn.prepareStatement(decreaseStockQuery)) {
                            decreaseStockStatement.setInt(1, currentTheaterId);

                            decreaseStockStatement.executeUpdate();
                            System.out.println("Old Theater stock decreased successfully");
                        }

                        // Menambah stok teater baru
                        String increaseStockQuery = "UPDATE t_theater SET stock = stock + 1 WHERE id = ?";
                        try (PreparedStatement increaseStockStatement = conn.prepareStatement(increaseStockQuery)) {
                            increaseStockStatement.setInt(1, chair.getTheaterId());

                            increaseStockStatement.executeUpdate();
                            System.out.println("New Theater stock increased successfully");
                        }

                        conn.commit();
                    } else {
                        System.out.println("Chair/Seat not found with ID : " + chair.getId());
                    }
                }
            }
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                System.out.println("Failed to rollback transaction: " + ex.getMessage());
            }
            System.out.println("Transaction failed: " + e.getMessage());
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                System.out.println("Failed to set auto-commit: " + ex.getMessage());
            }
        }
    }


    @Override
    public void deleteChair(Integer id) {
        try {
            conn.setAutoCommit(false);

            PreparedStatement checkIdStatement = conn.prepareStatement("SELECT COUNT(*) FROM t_seat WHERE id = ?");
            checkIdStatement.setInt(1, id);

            try (ResultSet result = checkIdStatement.executeQuery()) {
                if (result.next() && result.getInt(1) > 0) {
                    try (PreparedStatement getTheaterIdStatement = conn.prepareStatement("SELECT theater_id FROM t_seat WHERE id = ?")) {
                        getTheaterIdStatement.setInt(1, id);

                        try (ResultSet theaterIdResult = getTheaterIdStatement.executeQuery()) {
                            if (theaterIdResult.next()) {
                                int theaterId = theaterIdResult.getInt("theater_id");

                                try (PreparedStatement deleteChairStatement = conn.prepareStatement("DELETE FROM t_seat WHERE id = ?")) {
                                    deleteChairStatement.setInt(1, id);

                                    deleteChairStatement.executeUpdate();
                                    System.out.println("Chair/Seat deleted successfully");

                                    try (PreparedStatement updateStockStatement = conn.prepareStatement("UPDATE t_theater SET stock = stock - 1 WHERE id = ?")) {
                                        updateStockStatement.setInt(1, theaterId);

                                        updateStockStatement.executeUpdate();
                                        System.out.println("Theater stock decreased successfully");
                                    }
                                }
                            } else {
                                System.out.println("Error: Theater ID not found for Chair ID: " + id);
                            }
                        }
                    }
                } else {
                    System.out.println("Chair/Seat not found with ID: " + id);
                }
            }

            // Commit transaksi
            conn.commit();

        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                System.out.println("Failed to rollback transaction: " + ex.getMessage());
            }
            System.out.println("Transaction failed: " + e.getMessage());
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                System.out.println("Failed to set auto-commit: " + ex.getMessage());
            }
        }
    }

    @Override
    public Chair getChairById(Integer id) {
        Chair chair = new Chair();

        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM t_seat WHERE id = ?");
            ps.setInt(1, id);

            ResultSet result = ps.executeQuery();

            if (result.next()) {
                Integer id_seat = result.getInt("id");
                String seatNumber = result.getString("seat_number");
                Integer theaterId = result.getInt("theater_id");


                chair = new Chair(id_seat, seatNumber, theaterId);

            } else {
                System.out.println("Chair/Seat not found with ID: " + id);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return chair;
    }
}
