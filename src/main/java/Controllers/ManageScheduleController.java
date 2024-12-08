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
        setupButtonActions(); // Initialize the button actions
        loadSchedulesFromDatabase(); 
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
        String query = "SELECT DISTINCT departure_station AS station_name FROM schedule";  // Fetch departure stations
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                stations.add(resultSet.getString("station_name"));
            }
            departureStation.setItems(stations);
            arrivalStation.setItems(stations);
        } catch (SQLException e) {
            handleDatabaseError(e, "loading stations");
        }
    }


    private void loadTimes() {
        ObservableList<String> times = FXCollections.observableArrayList();

        // Generate times from 00:00:00 to 23:00:00 in 1-hour intervals
        for (int hour = 0; hour < 24; hour++) {
            String time = String.format("%02d:00:00", hour);
            times.add(time);
        }

        // Set the generated times to the combo boxes
        departureTime.setItems(times);
        arrivalTime.setItems(times);
    }

    private void loadCapacity() {
        ObservableList<String> capacities = FXCollections.observableArrayList("100", "200", "300", "400","500");
        capacity.setItems(capacities);
    }

    private void loadStatus() {
        ObservableList<String> statuses = FXCollections.observableArrayList("On Time", "Delayed", "Cancelled", "Active", "Inactive");
        status.setItems(statuses);
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
            String.valueOf(scheduleList.size() + 1),  // You might want to avoid using size as ID, use auto-increment from DB instead
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

    // Database Operations
    private void saveScheduleToDatabase(Schedule schedule) {
        String query = "INSERT INTO schedule (train_name, route, departure_station, arrival_station, " +
                "departure_date, arrival_date, departure_time, arrival_time, capacity, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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

            preparedStatement.executeUpdate();
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
