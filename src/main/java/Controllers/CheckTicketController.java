package Controllers;

import Models.Schedule;
import Models.Seat;
import Utils.DatabaseConnection;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class CheckTicketController {
    private static final Logger logger = Logger.getLogger(CheckTicketController.class.getName());

    
    @FXML
    private TableView<Schedule> trainTable;

    @FXML
    private TableView<Seat> seatTable;

    @FXML
    private DatePicker departureDatePicker;
    
    @FXML
    private Seat selectedSeat;
    
    @FXML
    private Schedule selectedTrain;
    
    @FXML
    private ComboBox<String> departureStationComboBox;

    @FXML
    private ComboBox<String> arrivalStationComboBox;

    @FXML
    private Button searchButton;
    
    private BookTicketController bookTicketController;

    private boolean openedFromSidebar;

    private final ObservableList<Schedule> trainList = FXCollections.observableArrayList();
    private final ObservableList<Seat> seatList = FXCollections.observableArrayList();

    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/railway_system";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "3005";

    public void initialize() {
        setupListeners();
        // Check for null references
        if (trainTable == null) {
            logger.log(Level.SEVERE, "trainTable is null during initialization");
        }
        if (seatTable == null) {
            logger.log(Level.SEVERE, "seatTable is null during initialization");
        }

        // Load data and setup tables
        try {
            loadStationData();
            setupTrainTableColumns();
            setupSeatTableColumns();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Initialization error", e);
        }
    }

    private void setupListeners() {
        if (trainTable != null) {
            trainTable.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    handleTrainSelection();
                }
            });
        } else {
            logger.log(Level.SEVERE, "trainTable is null during setup listeners");
        }

        if (seatTable != null) {
            seatTable.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    System.out.println("Seat double-click detected."); // Debug output
                    handleSeatSelection();
                }
            });
        } else {
            logger.log(Level.SEVERE, "seatTable is null during setup listeners");
        }
    }
    
    public BookTicketController getBookTicketController() {
        return bookTicketController;
    }
    
    public void setBookTicketController(BookTicketController bookTicketController) {
        this.bookTicketController = bookTicketController;
        if (bookTicketController == null) {
            logger.severe("bookTicketController is null when set.");
        }
    }
    
    public void setOpenedFromSidebar(boolean openedFromSidebar) {
        this.openedFromSidebar = openedFromSidebar;
    }

    @FXML
    private void handleSeatSelection() {
        System.out.println("Seat selection handler triggered."); // Debug output
        Seat selectedSeat = seatTable.getSelectionModel().getSelectedItem();
        Schedule selectedTrain = trainTable.getSelectionModel().getSelectedItem();

        if (selectedSeat == null) {
            showAlert("Please select a seat.");
            return;
        }

        if (selectedTrain == null) {
            showAlert("Please select a train first.");
            return;
        }

        // Update the BookTicketController with selected seat details
        if (bookTicketController != null) {
            bookTicketController.fillTicketDetails(
                departureDatePicker.getValue().toString(),
                selectedTrain.getTrainName(),
                selectedTrain.getDepartureStation(),
                selectedTrain.getArrivalStation(),
                selectedSeat.getCoachNumber(),
                selectedSeat.getClassType(),
                selectedSeat.getSeatNumber()
            );
            System.out.println("Ticket details filled."); // Debug output
        } else {
            System.out.println("bookTicketController is null."); // Debug output
        }

        // Navigate based on how CheckTicket was opened
        navigateToBookTicket();
    }

    private void navigateToBookTicket() {
        if (openedFromSidebar) {
            if (bookTicketController != null) {
                bookTicketController.loadBookTicketPage();
                System.out.println("Navigating to BookTicket page."); // Debug output
            }
        } else {
            Stage currentStage = (Stage) seatTable.getScene().getWindow();
            currentStage.close();
        }
    }

    
    private void loadStationData() {
        String query = "SELECT DISTINCT departure_station FROM schedule UNION SELECT DISTINCT arrival_station FROM schedule";
        try (Connection conn = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            departureStationComboBox.getItems().clear();
            arrivalStationComboBox.getItems().clear();

            while (rs.next()) {
                String station = rs.getString(1);
                departureStationComboBox.getItems().add(station);
                arrivalStationComboBox.getItems().add(station);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error while loading stations", e);
            showAlert("Database error: " + e.getMessage());
        }
    }

    private void setupTrainTableColumns() {
        trainTable.getColumns().clear();

        TableColumn<Schedule, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        idColumn.setPrefWidth(50);

        TableColumn<Schedule, String> trainNameColumn = new TableColumn<>("Train Name");
        trainNameColumn.setCellValueFactory(cellData -> cellData.getValue().trainNameProperty());
        trainNameColumn.setPrefWidth(70);

        TableColumn<Schedule, String> departureTimeColumn = new TableColumn<>("Departure Time");
        departureTimeColumn.setCellValueFactory(cellData -> cellData.getValue().departureTimeProperty());
        departureTimeColumn.setPrefWidth(100);

        TableColumn<Schedule, String> arrivalTimeColumn = new TableColumn<>("Arrival Time");
        arrivalTimeColumn.setCellValueFactory(cellData -> cellData.getValue().arrivalTimeProperty());
        arrivalTimeColumn.setPrefWidth(100);

        TableColumn<Schedule, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        statusColumn.setPrefWidth(90);

        trainTable.getColumns().addAll(idColumn, trainNameColumn, departureTimeColumn, arrivalTimeColumn, statusColumn);
    }

    @FXML
    public void handleSearch() {
        String departureDate = departureDatePicker.getValue() != null ? departureDatePicker.getValue().toString() : null;
        String departureStation = departureStationComboBox.getValue();
        String arrivalStation = arrivalStationComboBox.getValue();

        if (departureStation == null || arrivalStation == null || departureDate == null) {
            showAlert("Please fill all fields.");
            return;
        }

        searchTrains(departureStation, arrivalStation, departureDate);
    }

    private void searchTrains(String departureStation, String arrivalStation, String departureDate) {
        String query = "SELECT * FROM schedule WHERE departure_station = ? AND arrival_station = ? " +
                       "AND departure_date = ? AND status IN ('Active', 'Available', 'On Time', 'Delayed')";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, departureStation);
            stmt.setString(2, arrivalStation);
            stmt.setDate(3, Date.valueOf(departureDate));
            ResultSet rs = stmt.executeQuery();

            trainList.clear();
            while (rs.next()) {
                trainList.add(new Schedule(
                    rs.getString("id"),
                    rs.getString("train_name"),
                    rs.getString("route"),
                    rs.getString("departure_station"),
                    rs.getString("arrival_station"),
                    rs.getString("departure_date"),
                    rs.getString("arrival_date"),
                    rs.getString("departure_time"),
                    rs.getString("arrival_time"),
                    rs.getString("capacity"),
                    rs.getString("status")
                ));
            }

            trainTable.setItems(trainList);
            if (trainList.isEmpty()) {
                showAlert("No available trains found.");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error while searching trains", e);
            showAlert("Database error: " + e.getMessage());
        }
    }

    private void loadSeatsForTrain(String trainId) {
        String query = "SELECT id, seat_number, coach_number, class_type, train_id, is_available " +
                       "FROM seat WHERE train_id = ? AND is_available = TRUE"; // Filter for available seats

        try (Connection conn = DatabaseConnection.getConnection(); // Use the utility class for connection
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, trainId); // Use setString since trainId is a String
            ResultSet rs = stmt.executeQuery();

            seatList.clear();
            while (rs.next()) {
                String seatId = rs.getString("id");
                String seatNumber = rs.getString("seat_number");
                int coachNumber = rs.getInt("coach_number");
                String classType = rs.getString("class_type");
                boolean isAvailable = rs.getBoolean("is_available");

                // Create a new Seat object
                seatList.add(new Seat(
                    seatId,            // Seat ID
                    String.valueOf(trainId), // Train ID as String
                    String.valueOf(coachNumber), // Coach Number as String
                    classType,        // Class Type
                    seatNumber,       // Seat Number
                    isAvailable        // Availability
                ));
            }

            setupSeatTableColumns(); // Ensure this is defined to set up your table
            seatTable.setItems(seatList); // Bind the seat list to the table
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error while loading seats for train " + trainId, e);
            showAlert("Database error: " + e.getMessage());
        }
    }

    @FXML
    public void handleTrainSelection() {
        Schedule selectedTrain = trainTable.getSelectionModel().getSelectedItem();
        if (selectedTrain == null) {
            showAlert("Please select a train.");
            return;
        }
        loadSeatsForTrain(selectedTrain.getId());
    }

    private void setupSeatTableColumns() {
        seatTable.getColumns().clear();

        TableColumn<Seat, String> coachColumn = new TableColumn<>("Coach Number");
        coachColumn.setCellValueFactory(cellData -> cellData.getValue().coachNumberProperty());
        coachColumn.setPrefWidth(150);

        TableColumn<Seat, String> classColumn = new TableColumn<>("Class Type");
        classColumn.setCellValueFactory(cellData -> cellData.getValue().classTypeProperty());
        classColumn.setPrefWidth(200);

        TableColumn<Seat, String> seatNumberColumn = new TableColumn<>("Seat Number");  // Display seat number
        seatNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getSeatNumber())));
        seatNumberColumn.setPrefWidth(170);

        seatTable.getColumns().addAll(coachColumn, classColumn, seatNumberColumn);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
