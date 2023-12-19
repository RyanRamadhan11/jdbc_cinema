package com.enigma.repository.impl;

import com.enigma.configuration.DbConnector;
import com.enigma.entity.Customer;
import com.enigma.entity.Film;
import com.enigma.repository.FilmRepo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FilmRepoImpl implements FilmRepo {
    DbConnector dbConnector = new DbConnector();
    Connection conn = dbConnector.startConnect();

    @Override
    public List<Film> getAll() {
        List<Film> film = new ArrayList<>();

        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM t_film");
            ResultSet result = ps.executeQuery();

            while (result.next()) {
                Integer id = result.getInt("id");
                String title = result.getString("title");
                Integer duration = result.getInt("duration");
                String showDate = result.getString("show_date");
                Integer price = result.getInt("price");
                Integer rating_id = result.getInt("rating_id");

                film.add(new Film(id, title, duration, showDate, price, rating_id));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return film;
    }

    @Override
    public void addFilm(Film film) {
        try {
            String query = "INSERT INTO t_film (title, duration, show_date, price, rating_id) VALUES (?, ?, ?, ?, ?)";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, film.getTitle());
            ps.setInt(2, film.getDuration());

            java.sql.Date sqlDate = java.sql.Date.valueOf(film.getShowDate());
            ps.setDate(3, sqlDate);

            ps.setInt(4, film.getPrice());
            ps.setInt(5, film.getRatingId());

            ps.executeUpdate();
            System.out.println("Film added successfully");
        } catch (SQLException e) {
            System.out.println("Failed to add film: " + e.getMessage());
        }
    }

    @Override
    public void updateFilm(Film film) {
        try {
            // Memanggil metode getFilmById untuk mendapatkan film berdasarkan ID
            Film existingFilm = getFilmById(film.getId());

            // Memeriksa apakah film dengan ID yang diberikan ada atau tidak
            if (existingFilm.getId() != null) {
                // Film ditemukan, lakukan update
                String queryUpdate = "UPDATE t_film SET title = ?, duration = ?, show_date = ?, price = ?, rating_id = ? WHERE id = ?";
                try (PreparedStatement ps = conn.prepareStatement(queryUpdate)) {
                    ps.setString(1, film.getTitle());
                    ps.setInt(2, film.getDuration());

                    java.sql.Date sqlDate = java.sql.Date.valueOf(film.getShowDate());
                    ps.setDate(3, sqlDate);

                    ps.setInt(4, film.getPrice());
                    ps.setInt(5, film.getRatingId());
                    ps.setInt(6, film.getId());

                    ps.executeUpdate();
                    System.out.println("Film updated successfully");
                }
            } else {
                // Film tidak ditemukan
                System.out.println("Film not found with ID: " + film.getId());
            }
        } catch (SQLException e) {
            System.out.println("Failed to update film: " + e.getMessage());
        }
    }


//    @Override
//    public void updateFilm(Film film) {
//        try {
//            String queryCheckId = "SELECT COUNT(*) FROM t_film WHERE id = ?";
//            String queryUpdate = "UPDATE t_film SET title = ?, duration = ?, show_date = ?, price = ?, rating_id = ? WHERE id = ?";
//
//            PreparedStatement checkIdStatement = conn.prepareStatement(queryCheckId);
//            checkIdStatement.setInt(1, film.getId());
//
//            try (ResultSet result = checkIdStatement.executeQuery()) {
//                if (result.next() && result.getInt(1) > 0) {
//                    // ID ditemukan, lakukan update
//                    try (PreparedStatement ps = conn.prepareStatement(queryUpdate)) {
//                        ps.setString(1, film.getTitle());
//                        ps.setInt(2, film.getDuration());
//
//                        java.sql.Date sqlDate = java.sql.Date.valueOf(film.getShowDate());
//                        ps.setDate(3, sqlDate);
//
//                        ps.setInt(4, film.getPrice());
//                        ps.setInt(5, film.getRatingId());
//                        ps.setInt(6, film.getId());
//
//                        ps.executeUpdate();
//                        System.out.println("Film updated successfully");
//                    }
//                } else {
//                    // ID tidak ditemukan
//                    System.out.println("Film not found with ID : " + film.getId());
//                }
//            }
//        } catch (SQLException e) {
//            System.out.println("Failed to update film: " + e.getMessage());
//        }
//    }

    @Override
    public void deleteFilm(Integer id) {
        try {
            String queryCheckId = "SELECT COUNT(*) FROM t_film WHERE id = ?";
            String queryDelete = "DELETE FROM t_film WHERE id = ?";

            PreparedStatement checkIdStatement = conn.prepareStatement(queryCheckId);
            checkIdStatement.setInt(1, id);

            try (ResultSet result = checkIdStatement.executeQuery()) {
                if (result.next() && result.getInt(1) > 0) {
                    // ID ditemukan, lakukan delete
                    try (PreparedStatement ps = conn.prepareStatement(queryDelete)) {
                        ps.setInt(1, id);
                        ps.executeUpdate();
                        System.out.println("Film deleted successfully");
                    }
                } else {
                    // ID tidak ditemukan
                    System.out.println("Film not found with ID: " + id);
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to delete film: " + e.getMessage());
        }
    }

    @Override
    public Film getFilmById(Integer id) {
        Film film = new Film();

        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM t_film WHERE id = ?");
            ps.setInt(1, id);

            ResultSet result = ps.executeQuery();

            if (result.next()) {
                Integer id_film = result.getInt("id");
                String title = result.getString("title");
                Integer duration = result.getInt("duration");
                String showDate = result.getString("show_date");
                Integer price = result.getInt("price");
                Integer rating_id = result.getInt("rating_id");
                film = new Film(id_film, title, duration, showDate, price, rating_id);

            } else {
                System.out.println("Film not found with ID: " + id);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return film;
    }
}
