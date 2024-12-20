package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/railway_system"; // Update with your database URL
    private static final String USER = "root"; // Update with your database username
    private static final String PASSWORD = "3005"; // Update with your database password

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}