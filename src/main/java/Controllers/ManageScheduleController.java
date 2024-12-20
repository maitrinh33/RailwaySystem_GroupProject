package Controllers;

import Models.Schedule;
import java.io.IOException;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.util.logging.*;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import java.util.function.Function;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class ManageScheduleController implements Initializable {
    @FXML
    private TableView<Schedule> tableView;

    @FXML
    private ComboBox<String> trainName, route, departureStation, arrivalStation, capacity, status, departureTime, arrivalTime;

    @FXML
    private DatePicker departureDate, arrivalDate;

    @FXML
    private Button btnAdd, btnUpdate, btnDelete, btnClear;

    private final ObservableList<Schedule> scheduleList = FXCollections.observableArrayList();
    
    @FXML
    private TextField Search;
    
    // Add this at the class level
    private FilteredList<Schedule> filteredScheduleList;
    
    private Schedule selectedSchedule = null;

    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/railway_system"; 
    private static final String USERNAME = "root"; 
    private static final String PASSWORD = "3005"; 
    private static final Logger logger = Logger.getLogger(ManageScheduleController.class.getName());
    
    public void openSeatInventory() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SeatInventory.fxml"));
            Parent root = loader.load();

            SeatInventoryController seatInventoryController = loader.getController();
            if (selectedSchedule != null) {
                seatInventoryController.setTrainId(selectedSchedule.getId());
            }

            Stage stage = new Stage();
            stage.setTitle("Manage Seat Inventory");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load the FXML", e);
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTable();
        loadComboBoxData(); 
        setupButtonActions(); 
        loadSchedulesFromDatabase();
        
       // Initialize FilteredList
        filteredScheduleList = new FilteredList<>(scheduleList, b -> true);
        tableView.setItems(filteredScheduleList);

        // Add listener to the Search TextField
        Search.textProperty().addListener((observable, oldValue, newValue) -> {
            filterTable(newValue);
        });
    }
    
    private void filterTable(String searchText) {
        filteredScheduleList.setPredicate(schedule -> {
            // If no search text, display all schedules
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }

            // Compare schedule properties with the search text
            String lowerCaseFilter = searchText.toLowerCase();
            if(schedule.getId().toLowerCase().contains(lowerCaseFilter)) {
                return true; // Match found
            }
            if (schedule.getTrainName().toLowerCase().contains(lowerCaseFilter)) {
                return true; 
            } else if (schedule.getRoute().toLowerCase().contains(lowerCaseFilter)) {
                return true; 
            } else if (schedule.getDepartureStation().toLowerCase().contains(lowerCaseFilter)) {
                return true; 
            } else if (schedule.getArrivalStation().toLowerCase().contains(lowerCaseFilter)) {
                return true; 
            } else if (schedule.getDepartureDate().toLowerCase().contains(lowerCaseFilter)) {
                return true; 
            } else if (schedule.getArrivalDate().toLowerCase().contains(lowerCaseFilter)) {
                return true; 
            } else if (schedule.getDepartureTime().toLowerCase().contains(lowerCaseFilter)) {
                return true; 
            } else if (schedule.getArrivalTime().toLowerCase().contains(lowerCaseFilter)) {
                return true; 
            } else if (schedule.getCapacity().toLowerCase().contains(lowerCaseFilter)) {
                return true; 
            } else if (schedule.getStatus().toLowerCase().contains(lowerCaseFilter)) {
                return true; 
            }
            return false; 
        });
    }
    
    // Helper method for creating columns dynamically
    private TableColumn<Schedule, String> createColumn(String title, Function<Schedule, StringProperty> property) {
        TableColumn<Schedule, String> column = new TableColumn<>(title);
        column.setCellValueFactory(cellData -> property.apply(cellData.getValue()));
        return column;
    }

    private void setupTable() {
        tableView.getColumns().addAll(
            createColumn("ID", Schedule::idProperty),
            createColumn("Train Name", Schedule::trainNameProperty),
            createColumn("Route", Schedule::routeProperty),
            createColumn("Departure Station", Schedule::departureStationProperty),
            createColumn("Arrival Station", Schedule::arrivalStationProperty),
            createColumn("Departure Date", Schedule::departureDateProperty),
            createColumn("Arrival Date", Schedule::arrivalDateProperty),
            createColumn("Departure Time", Schedule::departureTimeProperty),
            createColumn("Arrival Time", Schedule::arrivalTimeProperty),
            createColumn("Capacity", Schedule::capacityProperty),
            createColumn("Status", Schedule::statusProperty)
        );
        tableView.setItems(scheduleList);
        tableView.setOnMouseClicked(this::onRowSelected);
    }

    private void loadComboBoxData() {
        loadTrainNames();
        loadRoutes();
        loadStations();
        loadTimes();
        loadCapacity();
        loadStatus();
    }

    private void loadTrainNames() {
        ObservableList<String> trainNames = FXCollections.observableArrayList();
        String query = "SELECT DISTINCT train_name FROM schedule";  // Fetch train names from the schedule table
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                trainNames.add(resultSet.getString("train_name"));
            }
            trainName.setItems(trainNames);
        } catch (SQLException e) {
            handleDatabaseError(e, "loading train names");
        }
    }

    private void loadRoutes() {
        ObservableList<String> routes = FXCollections.observableArrayList();
        String query = "SELECT DISTINCT route FROM schedule";  // Fetch routes from the schedule table
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                routes.add(resultSet.getString("route"));
            }
            route.setItems(routes);
        } catch (SQLException e) {
            handleDatabaseError(e, "loading routes");
        }
    }

    private void loadStations() {
        ObservableList<String> stations = FXCollections.observableArrayList();
        String query = "SELECT DISTINCT departure_station FROM schedule UNION SELECT DISTINCT arrival_station FROM schedule";

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                stations.add(resultSet.getString(1));  // Use index if aliasing is not necessary
            }
            departureStation.setItems(stations);
            arrivalStation.setItems(stations);
        } catch (SQLException e) {
            handleDatabaseError(e, "loading stations");
        }
    }


    private void loadTimes() {
        ObservableList<String> times = FXCollections.observableArrayList();
        String query = "SELECT DISTINCT departure_time FROM schedule"; // Fetch distinct departure times

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                times.add(resultSet.getString("departure_time"));
            }

            // Also fetch arrival times
            query = "SELECT DISTINCT arrival_time FROM schedule"; // Fetch distinct arrival times
            try (PreparedStatement arrivalStatement = connection.prepareStatement(query);
                 ResultSet arrivalResultSet = arrivalStatement.executeQuery()) {

                while (arrivalResultSet.next()) {
                    if (!times.contains(arrivalResultSet.getString("arrival_time"))) {
                        times.add(arrivalResultSet.getString("arrival_time"));
                    }
                }
            }

            departureTime.setItems(times);
            arrivalTime.setItems(times);
        } catch (SQLException e) {
            handleDatabaseError(e, "loading times");
        }
    }

    private void loadCapacity() {
        ObservableList<String> capacities = FXCollections.observableArrayList();
        String query = "SELECT DISTINCT capacity FROM schedule"; // Fetch distinct capacities

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                capacities.add(resultSet.getString("capacity"));
            }
            capacity.setItems(capacities);
        } catch (SQLException e) {
            handleDatabaseError(e, "loading capacity");
        }
    }

    private void loadStatus() {
        ObservableList<String> statuses = FXCollections.observableArrayList();
        String query = "SELECT DISTINCT status FROM schedule"; // Fetch distinct statuses

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                statuses.add(resultSet.getString("status"));
            }
            status.setItems(statuses);
        } catch (SQLException e) {
            handleDatabaseError(e, "loading status");
        }
    }

    private boolean validateInputs() {
        if (trainName.getValue() == null || route.getValue() == null || departureStation.getValue() == null ||
            arrivalStation.getValue() == null || departureDate.getValue() == null || arrivalDate.getValue() == null ||
            departureTime.getValue() == null || arrivalTime.getValue() == null || capacity.getValue() == null ||
            status.getValue() == null) {
            showAlert("Validation Error", "Please fill in all fields", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    private void addSchedule() {
        if (!validateInputs()) return;

        Schedule newSchedule = new Schedule(
            null,  // ID will be generated by the database
            trainName.getValue(),
            route.getValue(),
            departureStation.getValue(),
            arrivalStation.getValue(),
            departureDate.getValue().toString(),
            arrivalDate.getValue().toString(),
            departureTime.getValue(),
            arrivalTime.getValue(),
            capacity.getValue(),
            status.getValue()
        );
        
        scheduleList.add(newSchedule);
        saveScheduleToDatabase(newSchedule);
        clearInputs();
        showAlert("Success", "Schedule added successfully", Alert.AlertType.INFORMATION);  // Added success feedback
    }


    private void updateSchedule() {
        if (selectedSchedule == null) {
            showAlert("Error", "No schedule selected", Alert.AlertType.ERROR);
            return;
        }

        selectedSchedule.setTrainName(trainName.getValue());
        selectedSchedule.setRoute(route.getValue());
        selectedSchedule.setDepartureStation(departureStation.getValue());
        selectedSchedule.setArrivalStation(arrivalStation.getValue());
        selectedSchedule.setDepartureDate(departureDate.getValue().toString());
        selectedSchedule.setArrivalDate(arrivalDate.getValue().toString());
        selectedSchedule.setDepartureTime(departureTime.getValue());
        selectedSchedule.setArrivalTime(arrivalTime.getValue());
        selectedSchedule.setCapacity(capacity.getValue());
        selectedSchedule.setStatus(status.getValue());

        if (!validateInputs()) return;
        tableView.refresh();
        updateScheduleInDatabase(selectedSchedule);
        clearInputs();
    }

    private void deleteSchedule() {
        if (selectedSchedule != null) {
            scheduleList.remove(selectedSchedule);
            deleteScheduleFromDatabase(selectedSchedule);
            clearInputs();
        } else {
            showAlert("Error", "No schedule selected", Alert.AlertType.ERROR);
        }
    }

    private void clearInputs() {
        trainName.setValue(null);
        route.setValue(null);
        departureStation.setValue(null);
        arrivalStation.setValue(null);
        departureDate.setValue(null);
        arrivalDate.setValue(null);
        departureTime.setValue(null);
        arrivalTime.setValue(null);
        capacity.setValue(null);
        status.setValue(null);
        selectedSchedule = null;
        tableView.getSelectionModel().clearSelection(); 
    }

    private void onRowSelected(MouseEvent event) {
        selectedSchedule = tableView.getSelectionModel().getSelectedItem();
        if (selectedSchedule != null) {
            trainName.setValue(selectedSchedule.getTrainName());
            route.setValue(selectedSchedule.getRoute());
            departureStation.setValue(selectedSchedule.getDepartureStation());
            arrivalStation.setValue(selectedSchedule.getArrivalStation());
            departureDate.setValue(java.time.LocalDate.parse(selectedSchedule.getDepartureDate()));
            arrivalDate.setValue(java.time.LocalDate.parse(selectedSchedule.getArrivalDate()));
            arrivalTime.setValue(selectedSchedule.getArrivalTime());
            departureTime.setValue(selectedSchedule.getDepartureTime());
            capacity.setValue(selectedSchedule.getCapacity());
            status.setValue(selectedSchedule.getStatus());
        }
    }

    // Method to set actions for the buttons
    private void setupButtonActions() {
        btnAdd.setOnAction(e -> addSchedule());
        btnUpdate.setOnAction(e -> updateSchedule());
        btnDelete.setOnAction(e -> deleteSchedule());
        btnClear.setOnAction(e -> clearInputs());
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void handleDatabaseError(SQLException e, String action) {
        logger.log(Level.SEVERE, "Error while " + action, e);
        showAlert("Database Error", "Error occurred: " + e.getMessage(), Alert.AlertType.ERROR);
    }

    private void saveScheduleToDatabase(Schedule schedule) {
        String query = "INSERT INTO schedule (train_name, route, departure_station, arrival_station, " +
                "departure_date, arrival_date, departure_time, arrival_time, capacity, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, schedule.getTrainName());
            preparedStatement.setString(2, schedule.getRoute());
            preparedStatement.setString(3, schedule.getDepartureStation());
            preparedStatement.setString(4, schedule.getArrivalStation());
            preparedStatement.setString(5, schedule.getDepartureDate());
            preparedStatement.setString(6, schedule.getArrivalDate());
            preparedStatement.setString(7, schedule.getDepartureTime());
            preparedStatement.setString(8, schedule.getArrivalTime());
            preparedStatement.setString(9, schedule.getCapacity());
            preparedStatement.setString(10, schedule.getStatus());

            preparedStatement.executeUpdate();

            // Retrieve the generated ID
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    String generatedId = generatedKeys.getString(1);
                    schedule.setId(generatedId);  // Assuming you have a setId method in your Schedule class
                }
            }
        } catch (SQLException e) {
            handleDatabaseError(e, "saving schedule");
        }
    }

    private void updateScheduleInDatabase(Schedule schedule) {
        String query = "UPDATE schedule SET train_name = ?, route = ?, departure_station = ?, arrival_station = ?, " +
                "departure_date = ?, arrival_date = ?, departure_time = ?, arrival_time = ?, capacity = ?, status = ? WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, schedule.getTrainName());
            preparedStatement.setString(2, schedule.getRoute());
            preparedStatement.setString(3, schedule.getDepartureStation());
            preparedStatement.setString(4, schedule.getArrivalStation());
            preparedStatement.setString(5, schedule.getDepartureDate());
            preparedStatement.setString(6, schedule.getArrivalDate());
            preparedStatement.setString(7, schedule.getDepartureTime());
            preparedStatement.setString(8, schedule.getArrivalTime());
            preparedStatement.setString(9, schedule.getCapacity());
            preparedStatement.setString(10, schedule.getStatus());
            preparedStatement.setString(11, schedule.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            handleDatabaseError(e, "updating schedule");
        }
    }

    private void deleteScheduleFromDatabase(Schedule schedule) {
        String query = "DELETE FROM schedule WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, schedule.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            handleDatabaseError(e, "deleting schedule");
        }
    }

    private void loadSchedulesFromDatabase() {
        String query = "SELECT * FROM schedule";
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                Schedule schedule = new Schedule(
                    resultSet.getString("id"),
                    resultSet.getString("train_name"),
                    resultSet.getString("route"),
                    resultSet.getString("departure_station"),
                    resultSet.getString("arrival_station"),
                    resultSet.getString("departure_date"),
                    resultSet.getString("arrival_date"),
                    resultSet.getString("departure_time"),
                    resultSet.getString("arrival_time"),
                    resultSet.getString("capacity"),
                    resultSet.getString("status")
                );
                scheduleList.add(schedule);
            }
        } catch (SQLException e) {
            handleDatabaseError(e, "loading schedules");
        }
    }
}
