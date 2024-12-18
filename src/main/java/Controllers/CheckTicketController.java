package Controllers;

import Models.Schedule;
import Models.Seat;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
    private ComboBox<String> departureStationComboBox;

    @FXML
    private ComboBox<String> arrivalStationComboBox;

    @FXML
    private Button searchButton;

    private final ObservableList<Schedule> trainList = FXCollections.observableArrayList();
    private final ObservableList<Seat> seatList = FXCollections.observableArrayList();

    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/railway_system";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "3005";

    @FXML
    public void initialize() {
        setupListeners();
        try {
            loadStationData(); // Load station data when the controller initializes
            setupTrainTableColumns(); // Setup initial table columns
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
                    handleSeatSelection();
                }
            });
        } else {
            logger.log(Level.SEVERE, "seatTable is null during setup listeners");
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
        statusColumn.setPrefWidth(80);

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

    private void handleSeatSelection() {
        Seat selectedSeat = seatTable.getSelectionModel().getSelectedItem();
        if (selectedSeat == null) {
            showAlert("Please select a seat.");
            return;
        }

        Schedule selectedTrain = trainTable.getSelectionModel().getSelectedItem();
        if (selectedTrain == null) {
            showAlert("Please select a train first.");
            return;
        }

        // Load the BookTicket screen
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/BookTicket.fxml"));
            Parent bookTicketRoot = loader.load();
            Stage stage = (Stage) seatTable.getScene().getWindow();
            Scene scene = new Scene(bookTicketRoot);
            stage.setScene(scene);
            stage.show();

            // Get the BookTicketController to fill in the details
            BookTicketController bookTicketController = loader.getController();
            bookTicketController.fillTicketDetails(
                departureDatePicker.getValue().toString(),
                selectedTrain.getTrainName(),
                selectedTrain.getDepartureStation(),
                selectedTrain.getArrivalStation(),
                selectedSeat.getCoachNumber(),
                selectedSeat.getClassType(),
                selectedSeat.getSeatIds()
            );

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error loading Book Ticket screen", e);
            showAlert("Error loading the Book Ticket screen.");
        }
    }

    private void loadSeatsForTrain(String trainId) {
        String query = "SELECT * FROM seat_inventory WHERE train_id = ?";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, trainId);
            ResultSet rs = stmt.executeQuery();

            seatList.clear();
            while (rs.next()) {
                String seatIdsString = rs.getString("seat_ids");
                List<String> seatIds = List.of(seatIdsString.split(","));

                for (String seatId : seatIds) {
                    seatList.add(new Seat(
                        rs.getString("id"), // Assuming this is unique for the coach
                        rs.getString("train_id"),
                        rs.getInt("coach_number"),
                        rs.getString("class_type"),
                        rs.getInt("total_seats"),
                        rs.getInt("available_seats"),
                        List.of(seatId) // Create a single seat entry for each seat ID
                    ));
                }
            }

            setupSeatTableColumns();
            seatTable.setItems(seatList);
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
        coachColumn.setCellValueFactory(cellData -> cellData.getValue().coachNumberProperty().asString());
        coachColumn.setPrefWidth(80);

        TableColumn<Seat, String> classColumn = new TableColumn<>("Class Type");
        classColumn.setCellValueFactory(cellData -> cellData.getValue().classTypeProperty());
        classColumn.setPrefWidth(80);

        TableColumn<Seat, String> totalSeatsColumn = new TableColumn<>("Total Seats");
        totalSeatsColumn.setCellValueFactory(cellData -> cellData.getValue().totalSeatsProperty().asString());
        totalSeatsColumn.setPrefWidth(80);

        TableColumn<Seat, String> availableSeatsColumn = new TableColumn<>("Available Seats");
        availableSeatsColumn.setCellValueFactory(cellData -> cellData.getValue().availableSeatsProperty().asString());
        availableSeatsColumn.setPrefWidth(80);

        TableColumn<Seat, String> seatNumbersColumn = new TableColumn<>("Seat Numbers");
        seatNumbersColumn.setCellValueFactory(cellData -> {
            List<String> seatIds = cellData.getValue().getSeatIds();
            return new SimpleStringProperty(String.join(", ", seatIds)); 
        });
        seatNumbersColumn.setPrefWidth(80);

        seatTable.getColumns().addAll(coachColumn, classColumn, totalSeatsColumn, availableSeatsColumn, seatNumbersColumn);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private BookTicketController getBookTicketController() {
        // Implement logic to retrieve the instance of BookTicketController
        // This could involve accessing it through a singleton pattern, dependency injection, etc.
        return null; // Placeholder
    }
}