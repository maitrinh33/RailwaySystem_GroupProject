package Controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;
import static javafx.application.Application.launch;
import javafx.fxml.FXML;

public class TrainManagementSystem extends Application {

    private boolean isSidebarLoaded = true;  // Flag to track if the sidebar is already loaded
    @FXML
    private SidebarController sidebarController;
    @FXML
    private BookTicketController bookTicketController; // Reference to your BookTicketController

    @FXML
    public void initialize() {
        sidebarController.setBookTicketController(bookTicketController); // Pass the correct instance
    }
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

            // Get the SidebarController and set the BookTicketController
            sidebarController = mainLoader.getController();
            bookTicketController = new BookTicketController(); // Create instance
            sidebarController.setBookTicketController(bookTicketController); // Pass it to sidebar

            // Load the initial dashboard or other content
            sidebarController.navigateToDashboard(null);
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