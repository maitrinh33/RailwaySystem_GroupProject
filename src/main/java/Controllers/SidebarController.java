package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.control.Button;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;
import javafx.scene.control.ScrollPane;

public class SidebarController {

    @FXML
    private Button btnDashboard;
    @FXML
    private Button btnCheckTicket;
    @FXML
    private Button btnBookTicket;
    @FXML
    private Button btnManageSchedule;
    @FXML
    private Button btnBookingHistory;
    @FXML
    private Button btnReports;
    
    private BorderPane mainPane;  // This will hold the center content

    // Setter to inject the main pane from the parent controller
    public void setMainPane(BorderPane mainPane) {
        this.mainPane = mainPane;
        loadContent("/view/Dashboard.fxml");  // Load the default view
    }

    private static final Logger LOGGER = Logger.getLogger(SidebarController.class.getName());

    // This function loads the content dynamically in the center pane
    private void loadContent(String fxmlPath) {
        if (mainPane == null) {
            LOGGER.log(Level.SEVERE, "Main pane is not set.");
            return; // Avoid further execution if mainPane is null
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Pane newContent = loader.load();

            // Wrap the content in a ScrollPane to make it scrollable
            ScrollPane scrollPane = new ScrollPane(newContent);
            scrollPane.setFitToWidth(true);  // Ensure content fits width
            scrollPane.setFitToHeight(true); // Ensure content fits height

            // Set the ScrollPane as the center of the BorderPane
            mainPane.setCenter(scrollPane);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load FXML: " + fxmlPath, e);
        }
    }

    // Button actions for navigation
    @FXML
    public void navigateToDashboard(ActionEvent event) {
        loadContent("/view/Dashboard.fxml");
    }

    @FXML
    public void navigateToCheckTicket(ActionEvent event) {
        loadContent("/view/CheckTicket.fxml");
    }

    @FXML
    public void navigateToBookTicket(ActionEvent event) {
        loadContent("/view/BookTicket.fxml");
    }

    @FXML
    public void navigateToManageSchedule(ActionEvent event) {
        loadContent("/view/ManageSchedule.fxml");
    }

    @FXML
    public void navigateToBookingHistory(ActionEvent event) {
        loadContent("/view/BookingHistory.fxml");
    }

    @FXML
    public void navigateToReports(ActionEvent event) {
        loadContent("/view/Reports.fxml");
    }

    // Initialization method to handle any necessary setup
    public void initialize() {
        // This method can be used for additional setup if needed.
    }
}
