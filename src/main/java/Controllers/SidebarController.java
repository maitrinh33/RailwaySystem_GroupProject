package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Button;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;

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
    private Button btnReturnTicket;
    @FXML
    private Button btnReports;
    
    private BorderPane mainPane;  // This will hold the center content
    private BookTicketController bookTicketController; // Reference to your BookTicketController

    private static final Logger LOGGER = Logger.getLogger(SidebarController.class.getName());

    public void setMainPane(BorderPane mainPane) {
        this.mainPane = mainPane;
        LOGGER.info("Main pane set successfully.");
    }
    
    public BorderPane getMainPane() {
        return mainPane;
    }
    
    // Setter to inject the BookTicketController
    public void setBookTicketController(BookTicketController bookTicketController) {
        this.bookTicketController = bookTicketController;
        LOGGER.log(Level.INFO, "BookTicketController set successfully: {0}", bookTicketController);
    }
    
    // This function loads the content dynamically in the center pane
    private void loadContent(String fxmlPath) {
        if (mainPane == null) {
            LOGGER.log(Level.SEVERE, "Main pane is not set.");
            return; // Avoid further execution if mainPane is null
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            AnchorPane newContent = loader.load();

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
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CheckTicket.fxml"));
        AnchorPane newContent = loader.load();

        // Get the controller and set the BookTicketController
        CheckTicketController checkTicketController = loader.getController();
        checkTicketController.setBookTicketController(bookTicketController);
        checkTicketController.setOpenedFromSidebar(true); // Indicate opened from sidebar
        // Log for debugging
        if (checkTicketController.getBookTicketController() == null) {
            LOGGER.severe("BookTicketController is null after setting.");
        }

        // Wrap the content in a ScrollPane
        ScrollPane scrollPane = new ScrollPane(newContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        // Set the new content in the main pane
        mainPane.setCenter(scrollPane);
        LOGGER.info("Navigated to CheckTicket view successfully.");
    } catch (IOException e) {
        LOGGER.log(Level.SEVERE, "Failed to load CheckTicket view", e);
    }
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
    public void navigateToReturnTicket(ActionEvent event) {
        loadContent("/view/ReturnTicket.fxml");
    }
    
    @FXML
    public void navigateToReports(ActionEvent event) {
        loadContent("/view/Reports.fxml");
    }

    // Initialization method to handle any necessary setup
    public void initialize() {
        LOGGER.info("SidebarController initialized.");
    }
}