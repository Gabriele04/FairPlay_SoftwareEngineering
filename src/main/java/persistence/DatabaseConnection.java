package main.java.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private final Connection connection;

    private DatabaseConnection() {
        try {
            String url = System.getenv("DB_URL");
            if (url == null || url.isBlank()) {
                throw new IllegalStateException("DB_URL null o vuoto.");
            }

            String user = System.getenv("DB_USER");
            if (user == null || user.isBlank()) {
                throw new IllegalStateException("DB_USER null o  vuoto.");
            }

            String pass = System.getenv("DB_PASSWORD");
            if (pass == null) {
                throw new IllegalStateException("DB_PASSWORD null o  vuoto.");
            }

            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(url, user, pass);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Errore durante la connessione al database.", e);
        }
    }

    public static synchronized DatabaseConnection getInstance() {
        try {
            if (instance == null || instance.getConnection().isClosed()) {
                instance = new DatabaseConnection();
            }
        } catch (SQLException e) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}