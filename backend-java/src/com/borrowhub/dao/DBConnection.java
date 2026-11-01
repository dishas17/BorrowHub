package com.borrowhub.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * JDBC Database Connection Singleton for BorrowHub.
 */
public class DBConnection {
    // Configurable database credentials
    private static final String URL = "jdbc:mysql://localhost:3306/borrowhub?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "root"; // Update according to local MySQL installation

    static {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found! Make sure mysql-connector-j.jar is in WEB-INF/lib");
            e.printStackTrace();
        }
    }

    /**
     * Obtains a new database connection.
     *
     * @return Connection object
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Safely closes connection, statement, and resultset without throwing unhandled exceptions.
     */
    public static void close(AutoCloseable... resources) {
        for (AutoCloseable res : resources) {
            if (res != null) {
                try {
                    res.close();
                } catch (Exception ignored) {
                }
            }
        }
    }
}
