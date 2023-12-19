


package com.enigma.configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnector {
    private static Connection conn;

    public Connection startConnect() {

        // Informasi koneksi ke database
        String url = "jdbc:postgresql://localhost:5432/db_cinema";

        String username = System.getenv("USERNAME_DB");
        String password = System.getenv("PASSWORD_DB");

        try {
            conn = DriverManager.getConnection(url, username, password);
//            System.out.println("Connected to the database successfully\n");
        } catch (SQLException e) {
            System.out.println("Failed to connect to the database: " + e.getMessage());
        }
        return conn;
    }
}
