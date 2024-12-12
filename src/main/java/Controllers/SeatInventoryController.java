package Controllers;

import Models.Seat;
import Models.SeatTemplate;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.*;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class SeatInventoryController {

    @FXML
    private TableView<Seat> seatInventoryTable;
    @FXML
    private TextField txtTrainId;
    @FXML
    private TextField txtCoachNumber;
    @FXML
    private ComboBox<String> comboClassType;
    @FXML
    private TextField txtTotalSeats;
    @FXML
    private TextField txtAvailableSeats;
    @FXML
    private TextField txtSeatIds;
    @FXML
    private ComboBox<SeatTemplate> templateComboBox;

    private final ObservableList<Seat> seatList = FXCollections.observableArrayList();
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/railway_system";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "3005";
    private static final Logger logger = Logger.getLogger(SeatInventoryController.class.getName());

    @FXML
    public void initialize() {
        loadClassTypes();
        loadSeatTemplates(); 
        setupTableColumns();
        loadSeats();

        // Add listener to table selection
        seatInventoryTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateFields(newSelection);
            }
        });

        // Add listener to template selection
        templateComboBox.setOnAction(event -> loadTemplateData());
    }

    private void loadClassTypes() {
        comboClassType.getItems().addAll("First Class", "Business", "Economy");
    }

    private void loadSeatTemplates() {
        // Predefined seat templates
        List<SeatTemplate> templates = List.of(
            new SeatTemplate("First Class", 60, 60),
            new SeatTemplate("Business", 210, 210),
            new SeatTemplate("Economy", 30, 10)
        );

        templateComboBox.setItems(FXCollections.observableArrayList(templates));
    }
    
    private void loadTemplateData() {
        SeatTemplate selectedTemplate = templateComboBox.getValue();
        if (selectedTemplate != null) {
            txtTotalSeats.setText(String.valueOf(selectedTemplate.getTotalSeats()));
            txtAvailableSeats.setText(String.valueOf(selectedTemplate.getTotalSeats())); // Assuming all seats are available initially
            txtSeatIds.setText(generateSeatIds(selectedTemplate.getTotalSeats())); // Generate seat IDs based on the template
        }
    }

    private String generateSeatIds(int totalSeats) {
        List<String> seatIds = new ArrayList<>();
        String prefix = "S"; // Define seat ID prefix

        for (int i = 1; i <= totalSeats; i++) {
            seatIds.add(prefix + i); // Generate seat ID in the form S1, S2, ..., S20
        }
        return String.join(", ", seatIds);
    }
    
    private String generateSeatIdsForCoach(int coachNumber) {
        List<String> seatIds = new ArrayList<>();
        String prefix = "S"; // Define seat ID prefix

        for (int i = 1; i <= 20; i++) { // Assuming each coach has 20 seats
            seatIds.add(prefix + i); // Generate seat ID in the form S1, S2, ..., S20
        }
        return String.join(", ", seatIds);
    }


    private void setupTableColumns() {
        // Clear existing columns to prevent duplicates
        seatInventoryTable.getColumns().clear();

        TableColumn<Seat, String> colTrainId = new TableColumn<>("Train ID");
        colTrainId.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTrainId()));
        colTrainId.setPrefWidth(70); // Set fixed width
        colTrainId.setResizable(false); // Prevent resizing

        TableColumn<Seat, Integer> colCoachNumber = new TableColumn<>("Coach");
        colCoachNumber.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCoachNumber()).asObject());
        colCoachNumber.setPrefWidth(70); // Set fixed width
        colCoachNumber.setResizable(false); // Prevent resizing

        TableColumn<Seat, String> colClassType = new TableColumn<>("Class Type");
        colClassType.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClassType()));
        colClassType.setPrefWidth(100); // Set fixed width
        colClassType.setResizable(false); // Prevent resizing

        TableColumn<Seat, Integer> colTotalSeats = new TableColumn<>("Total Seats");
        colTotalSeats.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getTotalSeats()).asObject());
        colTotalSeats.setPrefWidth(100); // Set fixed width
        colTotalSeats.setResizable(false); // Prevent resizing

        TableColumn<Seat, Integer> colAvailableSeats = new TableColumn<>("Available Seats");
        colAvailableSeats.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getAvailableSeats()).asObject());
        colAvailableSeats.setPrefWidth(100); // Set fixed width
        colAvailableSeats.setResizable(false); // Prevent resizing

        TableColumn<Seat, String> colSeatIds = new TableColumn<>("Seat IDs");
        colSeatIds.setCellValueFactory(cellData -> 
            new SimpleStringProperty(String.join(", ", cellData.getValue().getSeatIds()))
        );
        colSeatIds.setPrefWidth(400); // Set fixed width
        colSeatIds.setResizable(false); // Prevent resizing

        // Add columns to the TableView
        seatInventoryTable.getColumns().addAll(colTrainId, colCoachNumber, colClassType, colTotalSeats, colAvailableSeats, colSeatIds);
    }

    public void setTrainId(String trainId) {
        txtTrainId.setText(trainId);
        loadSeats();
    }

    private void loadSeats() {
        String query = "SELECT * FROM seat_inventory WHERE train_id = ?";
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, txtTrainId.getText());
            ResultSet resultSet = preparedStatement.executeQuery();

            seatList.clear();
            while (resultSet.next()) {
                String seatIdString = resultSet.getString("seat_ids");
                List<String> seatIds = List.of(seatIdString.split(",\\s*")); // Split into list

                Seat seat = new Seat(
                    resultSet.getString("id"),
                    resultSet.getString("train_id"),
                    resultSet.getInt("coach_number"),
                    resultSet.getString("class_type"),
                    resultSet.getInt("total_seats"),
                    resultSet.getInt("available_seats"),
                    seatIds // Use the list
                );
                seatList.add(seat);
            }
            seatInventoryTable.setItems(seatList);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error loading seats", e);
            showAlert("Database Error", "Failed to load seats: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void populateFields(Seat seat) {
        txtTrainId.setText(seat.getTrainId());
        txtCoachNumber.setText(String.valueOf(seat.getCoachNumber()));
        comboClassType.setValue(seat.getClassType());
        txtTotalSeats.setText(String.valueOf(seat.getTotalSeats()));
        txtAvailableSeats.setText(String.valueOf(seat.getAvailableSeats()));
        
        // Convert List<String> to String
        txtSeatIds.setText(String.join(", ", seat.getSeatIds())); // Join the list into a single string
    }

    @FXML
    private void handleAdd() {
        String trainId = txtTrainId.getText();
        int coachNumber = Integer.parseInt(txtCoachNumber.getText());
        String classType = comboClassType.getValue();
        int totalSeats = Integer.parseInt(txtTotalSeats.getText());
        int availableSeats = Integer.parseInt(txtAvailableSeats.getText());
        String seatIdsInput = txtSeatIds.getText();

        // Check for duplicate coach
        if (isCoachExists(trainId, coachNumber)) {
            showAlert("Duplicate Entry", "A coach with this number already exists for the selected train.", Alert.AlertType.WARNING);
            return;
        }

        // Parse the seat IDs input
        List<String> seatIds = parseSeatRange(seatIdsInput);
        String seatIdsString = String.join(", ", seatIds);

        String insertQuery = "INSERT INTO seat_inventory (train_id, coach_number, class_type, total_seats, available_seats, seat_ids) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, trainId);
            preparedStatement.setInt(2, coachNumber);
            preparedStatement.setString(3, classType);
            preparedStatement.setInt(4, totalSeats);
            preparedStatement.setInt(5, availableSeats);
            preparedStatement.setString(6, seatIdsString);
            preparedStatement.executeUpdate();

            loadSeats();
            showAlert("Success", "Seat record added successfully!", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error adding seat record", e);
            showAlert("Database Error", "Failed to add seat record: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private boolean isCoachExists(String trainId, int coachNumber) {
        String query = "SELECT COUNT(*) FROM seat_inventory WHERE train_id = ? AND coach_number = ?";
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, trainId);
            preparedStatement.setInt(2, coachNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0; // Return true if any records exist
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error checking for existing coach", e);
        }
        return false;
    }

    @FXML
    private void handleAutoCreateCoaches() {
        String trainId = txtTrainId.getText();
        if (trainId.isEmpty()) {
            showAlert("Input Error", "Please enter a valid Train ID.", Alert.AlertType.WARNING);
            return;
        }

        for (int i = 1; i <= 11; i++) { // Create coaches from 1 to 11
            int coachNumber = i; // Coach number
            String classType = "Economy"; // Default class type
            int totalSeats = 20; // Total seats per coach
            int availableSeats = 20; // All seats available initially
            String seatIdsString = generateSeatIdsForCoach(coachNumber); // Generate seat IDs

            // Check for duplicate coach
            if (!isCoachExists(trainId, coachNumber)) {
                addCoachToDatabase(trainId, coachNumber, classType, totalSeats, availableSeats, seatIdsString);
            } else {
                showAlert("Duplicate Entry", "Coach " + coachNumber + " already exists for Train ID " + trainId + ".", Alert.AlertType.WARNING);
            }
        }
        loadSeats(); // Refresh the seat list
    }


    private void addCoachToDatabase(String trainId, int coachNumber, String classType, int totalSeats, int availableSeats, String seatIdsString) {
        String insertQuery = "INSERT INTO seat_inventory (train_id, coach_number, class_type, total_seats, available_seats, seat_ids) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, trainId);
            preparedStatement.setInt(2, coachNumber);
            preparedStatement.setString(3, classType);
            preparedStatement.setInt(4, totalSeats);
            preparedStatement.setInt(5, availableSeats);
            preparedStatement.setString(6, seatIdsString);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error adding coach to database", e);
            showAlert("Database Error", "Failed to add coach to database: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleUpdate() {
        Seat selectedSeat = seatInventoryTable.getSelectionModel().getSelectedItem();
        if (selectedSeat == null) {
            showAlert("Selection Error", "Please select a seat record to update.", Alert.AlertType.WARNING);
            return;
        }

        String seatId = selectedSeat.getId();
        String trainId = txtTrainId.getText();
        int coachNumber = Integer.parseInt(txtCoachNumber.getText());
        String classType = comboClassType.getValue();
        int totalSeats = Integer.parseInt(txtTotalSeats.getText());
        int availableSeats = Integer.parseInt(txtAvailableSeats.getText());
        String seatIdsInput = txtSeatIds.getText();

        // Parse the seat IDs input
        List<String> seatIds = parseSeatRange(seatIdsInput);
        String seatIdsString = String.join(", ", seatIds);

        String updateQuery = "UPDATE seat_inventory SET train_id = ?, coach_number = ?, class_type = ?, total_seats = ?, available_seats = ?, seat_ids = ? WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, trainId);
            preparedStatement.setInt(2, coachNumber);
            preparedStatement.setString(3, classType);
            preparedStatement.setInt(4, totalSeats);
            preparedStatement.setInt(5, availableSeats);
            preparedStatement.setString(6, seatIdsString);
            preparedStatement.setString(7, seatId);
            preparedStatement.executeUpdate();

            loadSeats();
            showAlert("Success", "Seat record updated successfully!", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating seat record", e);
            showAlert("Database Error", "Failed to update seat record: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleDelete() {
        Seat selectedSeat = seatInventoryTable.getSelectionModel().getSelectedItem();
        if (selectedSeat == null) {
            showAlert("Selection Error", "Please select a seat record to delete.", Alert.AlertType.WARNING);
            return;
        }

        String seatId = selectedSeat.getId();
        String deleteQuery = "DELETE FROM seat_inventory WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
            preparedStatement.setString(1, seatId);
            preparedStatement.executeUpdate();

            loadSeats();
            showAlert("Success", "Seat record deleted successfully!", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting seat record", e);
            showAlert("Database Error", "Failed to delete seat record: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private List<String> parseSeatRange(String seatRange) {
        List<String> seatIds = new ArrayList<>();

        // Split the input by commas to handle multiple ranges/IDs
        String[] ranges = seatRange.split(",\\s*");

        for (String range : ranges) {
            // Check if it's a range (contains a dash)
            if (range.contains("-")) {
                String[] parts = range.split("-");
                if (parts.length == 2) {
                    String prefix = parts[0].replaceAll("[0-9]", ""); // Get prefix like 'S'

                    // Extract start and end numbers
                    int start = Integer.parseInt(parts[0].replaceAll("[^0-9]", ""));
                    int end = Integer.parseInt(parts[1].replaceAll("[^0-9]", ""));

                    // Generate individual seat IDs for the range
                    for (int i = start; i <= end; i++) {
                        seatIds.add(prefix + i); // Construct seat ID
                    }
                }
            } else {
                // If it's not a range, treat it as a single ID
                seatIds.add(range.trim()); // Add directly, trimming whitespace
            }
        }

        return seatIds;
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}