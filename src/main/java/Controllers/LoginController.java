package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;  // Import BorderPane
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (validateLogin(username, password)) {
            // Successful login, load the main application (Sidebar with Dashboard)
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Sidebar.fxml"));
                BorderPane root = loader.load();

                // Extract the main content area (the center of the BorderPane)
                BorderPane mainPane = new BorderPane();

                // Inject the mainPane into the SidebarController
                SidebarController sidebarController = loader.getController();
                sidebarController.setMainPane(mainPane);

                // Set the center of the BorderPane (root) as the main content area
                root.setCenter(mainPane);

                // Load the Dashboard.fxml into the mainPane
                sidebarController.navigateToDashboard(null);

                // Set the Scene to the main app
                Stage stage = (Stage) usernameField.getScene().getWindow(); // Get the current stage
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Train Management System");
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Failed login attempt
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Login Failed");
            alert.setHeaderText(null);
            alert.setContentText("Invalid username or password");
            alert.showAndWait();
        }
    }

    private boolean validateLogin(String username, String password) {
        String url = "jdbc:mysql://localhost:3306/railway_system"; // Your database URL
        String dbUsername = "root";  // MySQL username
        String dbPassword = "3005";  // MySQL password

        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Get the stored password from the database
                String storedPassword = resultSet.getString("password");

                // Simply compare the input password with the stored password
                return password.equals(storedPassword);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; // Return false if no matching user or invalid credentials
    }
}
