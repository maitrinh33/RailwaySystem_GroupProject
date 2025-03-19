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

public class MainApp extends Application {

    private boolean isSidebarLoaded = true; 
    @FXML
    private SidebarController sidebarController;
    @FXML
    private BookTicketController bookTicketController; 

    @FXML
    public void initialize() {
        sidebarController.setBookTicketController(bookTicketController); 
    }
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
            AnchorPane loginRoot = loginLoader.load();

            Scene loginScene = new Scene(loginRoot);
            primaryStage.setScene(loginScene);
            primaryStage.setTitle("Login - Train Management System");
            primaryStage.show();
            loginLoader.getController();  
            primaryStage.setOnHidden(event -> loadMainInterface(primaryStage));

        } catch (IOException e) {
            System.err.println("Error loading FXML file: " + e.getMessage());
        }
    }

    private void loadMainInterface(Stage primaryStage) {
        if (!isSidebarLoaded) {  
            try {
                FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/view/Sidebar.fxml"));
                BorderPane mainRoot = mainLoader.load();

                Scene mainScene = new Scene(mainRoot);
                primaryStage.setScene(mainScene);
                primaryStage.setTitle("Train Management System");
                primaryStage.show();

            sidebarController = mainLoader.getController();
            bookTicketController = new BookTicketController(); 
            sidebarController.setBookTicketController(bookTicketController); 
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