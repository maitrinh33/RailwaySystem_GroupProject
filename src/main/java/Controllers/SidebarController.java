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
    
    private BorderPane mainPane;  
    private BookTicketController bookTicketController; 

    private static final Logger LOGGER = Logger.getLogger(SidebarController.class.getName());

    public void setMainPane(BorderPane mainPane) {
        this.mainPane = mainPane;
        LOGGER.info("Main pane set successfully.");
    }
    
    public BorderPane getMainPane() {
        return mainPane;
    }
    
    public void setBookTicketController(BookTicketController bookTicketController) {
        this.bookTicketController = bookTicketController;
        LOGGER.log(Level.INFO, "BookTicketController set successfully: {0}", bookTicketController);
    }
    
    private void loadContent(String fxmlPath) {
        if (mainPane == null) {
            LOGGER.log(Level.SEVERE, "Main pane is not set.");
            return; 
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            AnchorPane newContent = loader.load();

            ScrollPane scrollPane = new ScrollPane(newContent);
            scrollPane.setFitToWidth(true);  
            scrollPane.setFitToHeight(true); 

            
            mainPane.setCenter(scrollPane);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load FXML: " + fxmlPath, e);
        }
    }

    @FXML
    public void navigateToDashboard(ActionEvent event) {
        loadContent("/view/Dashboard.fxml");
    }

@FXML
public void navigateToCheckTicket(ActionEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CheckTicket.fxml"));
        AnchorPane newContent = loader.load();

        CheckTicketController checkTicketController = loader.getController();
        checkTicketController.setBookTicketController(bookTicketController);
        checkTicketController.setOpenedFromSidebar(true); 

        ScrollPane scrollPane = new ScrollPane(newContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

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

    public void initialize() {
        LOGGER.info("SidebarController initialized.");
    }
}