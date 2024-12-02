package Controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;
import static javafx.application.Application.launch;

public class TrainManagementSystem extends Application {

    private boolean isSidebarLoaded = false;  // Flag to track if the sidebar is already loaded

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the Login.fxml (AnchorPane layout)
            FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
            AnchorPane loginRoot = loginLoader.load();

            // Create and set the Scene for the login
            Scene loginScene = new Scene(loginRoot);
            primaryStage.setScene(loginScene);
            primaryStage.setTitle("Login - Train Management System");
            primaryStage.show();

            // After successful login, load the Sidebar with Dashboard content
            // Example: After login, you can switch to the main content (Sidebar.fxml)
            loginLoader.getController();  // Access the LoginController if needed for logic

            // Assuming you have a method or event listener in LoginController to handle successful login
            // After login is successful, load the next view (e.g., Sidebar.fxml)
            // For now, just calling a hypothetical method to handle that.
            
            // Let's assume after login, we switch to the main screen with Sidebar
            primaryStage.setOnHidden(event -> loadMainInterface(primaryStage));

        } catch (IOException e) {
            System.err.println("Error loading FXML file: " + e.getMessage());
        }
    }

    private void loadMainInterface(Stage primaryStage) {
        if (!isSidebarLoaded) {  // Check if the sidebar has already been loaded
            try {
                // Load the Sidebar.fxml (which will use BorderPane layout)
                FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/view/Sidebar.fxml"));
                BorderPane mainRoot = mainLoader.load();

                // Create and set the Scene for the main interface
                Scene mainScene = new Scene(mainRoot);
                primaryStage.setScene(mainScene);
                primaryStage.setTitle("Train Management System");
                primaryStage.show();

                // Set up any controller logic if needed
                SidebarController sidebarController = mainLoader.getController();
                // Assuming you have a method to handle loading the content (Dashboard, etc.)
                sidebarController.navigateToDashboard(null);  // Example of loading a view into the main pane

                // Mark the sidebar as loaded
                isSidebarLoaded = true;

            } catch (IOException e) {
                System.err.println("Error loading FXML file: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
