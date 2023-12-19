package com.enigma.repository.impl;

import com.enigma.configuration.DbConnector;
import com.enigma.entity.Ticket;
import com.enigma.repository.TicketRepo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TicketRepoImpl implements TicketRepo {

    DbConnector dbConnector = new DbConnector();
    Connection conn = dbConnector.startConnect();

    @Override
    public List<Ticket> getAll() {
        List<Ticket> tickets = new ArrayList<>();

        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM trx_ticket");
            ResultSet result = ps.executeQuery();

            while (result.next()) {
                Integer id = result.getInt("id");
                Integer seatId = result.getInt("seat_id");
                Integer customerId = result.getInt("customer_id");

                tickets.add(new Ticket(id, seatId, customerId));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return tickets;
    }

    private Integer calculateCustomerAge(Integer id) {
        int customerAge = 0;

        try {
            String query = "SELECT birth_date FROM m_customer WHERE id = ?";
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setInt(1, id);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    Date birthDate = resultSet.getDate("birth_date");
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(birthDate);

                    Integer birthYear = calendar.get(Calendar.YEAR);
                    Integer currentYear = Calendar.getInstance().get(Calendar.YEAR);

                    customerAge = currentYear - birthYear;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return customerAge;
    }

    private Integer getFilmRatingBySeatId(Integer id) {
        int filmRating = 0;
        try {
            String query = "SELECT f.rating_id FROM t_seat s " +
                    "JOIN t_theater t ON s.theater_id = t.id " +
                    "JOIN t_film f ON t.film_id = f.id " +
                    "WHERE s.id = ?";
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setInt(1, id);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    filmRating = resultSet.getInt("rating_id");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return filmRating;
    }

    private Integer getTheaterIdBySeatId(Integer id) {
        int theaterId = 0;

        try {
            String query = "SELECT theater_id FROM t_seat WHERE id = ?";
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setInt(1, id);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    theaterId = resultSet.getInt("theater_id");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return theaterId;
    }

    private boolean validateRating(Integer customerId, Integer seatId) {
        Integer customerAge = calculateCustomerAge(customerId);
        Integer filmRating = getFilmRatingBySeatId(seatId);

        switch (filmRating) {
            case 1: // Rating A: Umum
                return true;
            case 2: // Rating BO: Kurang dari 13 tahun tidak boleh
                return customerAge >= 13;
            case 3: // Rating R: Kurang dari 18 tahun tidak boleh
                return customerAge >= 18;
            case 4: // Rating D: Sama dengan atau lebih dari 21 tahun diperbolehkan
                return customerAge >= 21;
            default:
                return false;
        }
    }

    private Integer getCurrentStock(Integer id) {
        try {
            String query = "SELECT stock FROM t_theater WHERE id = ?";
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setInt(1, id);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("stock");
                    } else {
                        System.out.println("Theater not found with ID: " + id);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting current stock: " + e.getMessage());
        }
        return 0;
    }


    @Override
    public void addTicket(Ticket ticket) {
        try {
            // Menonaktifkan mode otomatis commit
            conn.setAutoCommit(false);

            Integer theaterId = getTheaterIdBySeatId(ticket.getSeatId());
            Integer currentStock = getCurrentStock(theaterId);

            // Validasi rating
            if (!validateRating(ticket.getCustomerId(), ticket.getSeatId())) {
                System.out.println("Customer does not meet the required rating criteria. Ticket purchase failed.");
                return;
            }

            if (currentStock > 0) {
                // Menambahkan tiket
                String insertTicketQuery = "INSERT INTO trx_ticket (seat_id, customer_id) VALUES (?, ?)";
                try (PreparedStatement insertTicketStatement = conn.prepareStatement(insertTicketQuery)) {
                    insertTicketStatement.setInt(1, ticket.getSeatId());
                    insertTicketStatement.setInt(2, ticket.getCustomerId());

                    insertTicketStatement.executeUpdate();
                    System.out.println("Ticket added successfully");
                }

                // Mengurangi stock bangku pada theater
                String updateStockQuery = "UPDATE t_theater SET stock = stock - 1 WHERE id = ?";
                try (PreparedStatement updateStockStatement = conn.prepareStatement(updateStockQuery)) {
                    updateStockStatement.setInt(1, theaterId);

                    updateStockStatement.executeUpdate();
                    System.out.println("Theater stock decreased successfully");
                }
            } else {
                // Rollback transaksi jika stok bangku tidak mencukupi
                System.out.println("Theater stock is insufficient. Ticket purchase failed.");
                conn.rollback();
                return;
            }

            // Commit transaksi
            conn.commit();

        } catch (SQLException e) {
            // Rollback transaksi jika terjadi kesalahan
            try {
                conn.rollback();
            } catch (SQLException ex) {
                System.out.println("Failed to rollback transaction: " + ex.getMessage());
            }
            System.out.println("Transaction failed: " + e.getMessage());
        } finally {
            try {
                // Mengaktifkan kembali mode otomatis commit
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                System.out.println("Failed to set auto-commit: " + ex.getMessage());
            }
        }
    }


    @Override
    public void updateTicket(Ticket ticket) {
        try {
            conn.setAutoCommit(false);

            String queryCheckId = "SELECT COUNT(*) FROM trx_ticket WHERE id = ?";
            PreparedStatement checkIdStatement = conn.prepareStatement(queryCheckId);
            checkIdStatement.setInt(1, ticket.getId());

            try (ResultSet result = checkIdStatement.executeQuery()) {
                if (result.next() && result.getInt(1) > 0) {
                    Integer oldSeatId = null;

                    // Dapatkan seat_id sebelum pembaruan
                    String queryGetSeatId = "SELECT seat_id FROM trx_ticket WHERE id = ?";
                    try (PreparedStatement getSeatIdStatement = conn.prepareStatement(queryGetSeatId)) {
                        getSeatIdStatement.setInt(1, ticket.getId());
                        ResultSet seatIdResultSet = getSeatIdStatement.executeQuery();

                        if (seatIdResultSet.next()) {
                            oldSeatId = seatIdResultSet.getInt("seat_id");
                        }
                    }

                    // Validasi rating
                    if (!validateRating(ticket.getCustomerId(), ticket.getSeatId())) {
                        System.out.println("Customer does not meet the required rating criteria. Ticket update failed.");
                        return;
                    }

                    // Mengupdate tiket
                    String updateTicketQuery = "UPDATE trx_ticket SET seat_id = ?, customer_id = ? WHERE id = ?";
                    try (PreparedStatement updateTicketStatement = conn.prepareStatement(updateTicketQuery)) {
                        updateTicketStatement.setInt(1, ticket.getSeatId());
                        updateTicketStatement.setInt(2, ticket.getCustomerId());
                        updateTicketStatement.setInt(3, ticket.getId());

                        updateTicketStatement.executeUpdate();
                        System.out.println("Ticket updated successfully");

                        // Dapatkan seat_id setelah pembaruan
                        Integer newSeatId = ticket.getSeatId();

                        if (oldSeatId != null && !oldSeatId.equals(newSeatId)) {

                            String updateStockQuery = "UPDATE t_theater SET stock = stock + 1 WHERE id = (SELECT theater_id FROM t_seat WHERE id = ?)";
                            try (PreparedStatement updateStockStatement = conn.prepareStatement(updateStockQuery)) {
                                updateStockStatement.setInt(1, oldSeatId);

                                updateStockStatement.executeUpdate();
                                System.out.println("Theater stock updated successfully for old seat_id");
                            }

                            String updateNewStockQuery = "UPDATE t_theater SET stock = stock - 1 WHERE id = (SELECT theater_id FROM t_seat WHERE id = ?)";
                            try (PreparedStatement updateNewStockStatement = conn.prepareStatement(updateNewStockQuery)) {
                                updateNewStockStatement.setInt(1, newSeatId);

                                updateNewStockStatement.executeUpdate();
                                System.out.println("Theater stock updated successfully for new seat_id");
                            }
                        }
                    }

                    conn.commit();
                } else {
                    System.out.println("Ticket not found with ID: " + ticket.getId());
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
    public void deleteTicket(Integer id) {
        try {
            // Menonaktifkan mode otomatis commit
            conn.setAutoCommit(false);

            Integer seatId = null;

            String queryCheckId = "SELECT COUNT(*) FROM trx_ticket WHERE id = ?";

            PreparedStatement checkIdStatement = conn.prepareStatement(queryCheckId);
            checkIdStatement.setInt(1, id);

            try (ResultSet result = checkIdStatement.executeQuery()) {
                if (result.next() && result.getInt(1) > 0) {

                    // Mendapatkan (seatId) dari tiket yang akan dihapus
                    String querySeatId = "SELECT seat_id FROM trx_ticket WHERE id = ?";
                    try (PreparedStatement psSeatId = conn.prepareStatement(querySeatId)) {
                        psSeatId.setInt(1, id);

                        try (ResultSet resultSeatId = psSeatId.executeQuery()) {
                            if (resultSeatId.next()) {
                                seatId = resultSeatId.getInt("seat_id");
                            }
                        }
                    }

                    if (seatId != null) {
                        // Menghapus tiket
                        String queryDelete = "DELETE FROM trx_ticket WHERE id = ?";
                        try (PreparedStatement psDelete = conn.prepareStatement(queryDelete)) {
                            psDelete.setInt(1, id);
                            psDelete.executeUpdate();
                            System.out.println("Ticket deleted successfully");
                        }

                        // Mengembalikan stok di theater yang sesuai
                        String queryUpdateStock = "UPDATE t_theater SET stock = stock + 1 WHERE id = (SELECT theater_id FROM t_seat WHERE id = ?)";
                        try (PreparedStatement psUpdateStock = conn.prepareStatement(queryUpdateStock)) {
                            psUpdateStock.setInt(1, seatId);

                            psUpdateStock.executeUpdate();
                            System.out.println("Theater stock updated successfully");
                        }

                        // Commit transaksi
                        conn.commit();
                    }
                } else {
                    // ID tidak ditemukan
                    System.out.println("Ticket not found with ID: " + id);
                }
            }
        } catch (SQLException e) {
            // Rollback transaksi jika terjadi kesalahan
            try {
                conn.rollback();
            } catch (SQLException ex) {
                System.out.println("Failed to rollback transaction: " + ex.getMessage());
            }
            System.out.println("Transaction failed: " + e.getMessage());
        } finally {
            try {
                // Mengaktifkan kembali mode otomatis commit
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                System.out.println("Failed to set auto-commit: " + ex.getMessage());
            }
        }
    }

    @Override
    public Ticket getTicketById(Integer id) {
        Ticket ticket = new Ticket();

        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM trx_ticket WHERE id = ?");
            ps.setInt(1, id);

            ResultSet result = ps.executeQuery();

            if (result.next()) {
                Integer id_ticket = result.getInt("id");
                Integer seatId = result.getInt("seat_id");
                Integer customerId = result.getInt("customer_id");

                ticket = new Ticket(id_ticket, seatId, customerId);
            } else {
                System.out.println("Ticket not found with ID: " + id);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return ticket;
    }
}
