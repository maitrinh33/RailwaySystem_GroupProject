package Models;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import Utils.DatabaseConnection;

public class Schedule {
    private static final Logger logger = Logger.getLogger(Schedule.class.getName());

    private final StringProperty id;
    private final StringProperty trainName;
    private final StringProperty route;
    private final StringProperty departureStation;
    private final StringProperty arrivalStation;
    private final StringProperty departureDate;
    private final StringProperty arrivalDate;
    private final StringProperty departureTime;
    private final StringProperty arrivalTime;
    private final StringProperty capacity;
    private final StringProperty status;

    // Constructor
    public Schedule(String id, String trainName, String route, String departureStation, String arrivalStation,
                    String departureDate, String arrivalDate, String departureTime, String arrivalTime, 
                    String capacity, String status) {
        this.id = new SimpleStringProperty(id);
        this.trainName = new SimpleStringProperty(trainName);
        this.route = new SimpleStringProperty(route);
        this.departureStation = new SimpleStringProperty(departureStation);
        this.arrivalStation = new SimpleStringProperty(arrivalStation);
        this.departureDate = new SimpleStringProperty(departureDate);
        this.arrivalDate = new SimpleStringProperty(arrivalDate);
        this.departureTime = new SimpleStringProperty(departureTime);
        this.arrivalTime = new SimpleStringProperty(arrivalTime);
        this.capacity = new SimpleStringProperty(capacity);
        this.status = new SimpleStringProperty(status);
    }
    
    // Simplified Schedule Constructor for Basic Data
    public Schedule(String trainName, String route) {
        this.id = null; // ID is not needed for tickets in this simplified version
        this.trainName = new SimpleStringProperty(trainName);
        this.route = new SimpleStringProperty(route);
        this.departureStation = null; // Not required for this use case
        this.arrivalStation = null;  // Not required for this use case
        this.departureDate = null;
        this.arrivalDate = null;
        this.departureTime = null;
        this.arrivalTime = null;
        this.capacity = null;
        this.status = null;
    }

    
    // Getters and Setters for JavaFX Binding
    public StringProperty idProperty() {
        return id;
    }

    public StringProperty trainNameProperty() {
        return trainName;
    }

    public StringProperty routeProperty() {
        return route;
    }

    public StringProperty departureStationProperty() {
        return departureStation;
    }

    public StringProperty arrivalStationProperty() {
        return arrivalStation;
    }

    public StringProperty departureDateProperty() {
        return departureDate;
    }

    public StringProperty arrivalDateProperty() {
        return arrivalDate;
    }

    public StringProperty departureTimeProperty() {
        return departureTime;
    }

    public StringProperty arrivalTimeProperty() {
        return arrivalTime;
    }

    public StringProperty capacityProperty() {
        return capacity;
    }

    public StringProperty statusProperty() {
        return status;
    }

    // Regular Getters
    public String getId() {
        return id.get();
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public String getTrainName() {
        return trainName.get();
    }

    public void setTrainName(String trainName) {
        this.trainName.set(trainName);
    }

    public String getRoute() {
        return route.get();
    }

    public void setRoute(String route) {
        this.route.set(route);
    }

    public String getDepartureStation() {
        return departureStation.get();
    }

    public void setDepartureStation(String departureStation) {
        this.departureStation.set(departureStation);
    }

    public String getArrivalStation() {
        return arrivalStation.get();
    }

    public void setArrivalStation(String arrivalStation) {
        this.arrivalStation.set(arrivalStation);
    }

    public String getDepartureDate() {
        return departureDate.get();
    }

    public void setDepartureDate(String departureDate) {
        this.departureDate.set(departureDate);
    }

    public String getArrivalDate() {
        return arrivalDate.get();
    }

    public void setArrivalDate(String arrivalDate) {
        this.arrivalDate.set(arrivalDate);
    }

    public String getDepartureTime() {
        return departureTime.get();
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime.set(departureTime);
    }

    public String getArrivalTime() {
        return arrivalTime.get();
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime.set(arrivalTime);
    }

    public String getCapacity() {
        return capacity.get();
    }

    public void setCapacity(String capacity) {
        this.capacity.set(capacity);
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    
    public static List<Schedule> getAllSchedules() {
        List<Schedule> schedules = new ArrayList<>();
        String sql = "SELECT s.id, t.name AS train_name, r.source_station, r.destination_station, "
                     + "s.departure_date, s.arrival_date, s.departure_time, s.arrival_time, "
                     + "s.capacity, s.status "
                     + "FROM schedule s "
                     + "JOIN trains t ON s.train_id = t.id "
                     + "JOIN routes r ON s.route_id = r.id";

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                Schedule schedule = createScheduleFromResultSet(resultSet);
                schedules.add(schedule);
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error while fetching schedules", e);
        }

        return schedules;
    }
    
        private static Schedule createScheduleFromResultSet(ResultSet resultSet) throws SQLException {
        return new Schedule(
            resultSet.getString("id"),
            resultSet.getString("train_name"),
            resultSet.getString("departure_station") + " - " + resultSet.getString("arrival_station"),
            resultSet.getString("departure_station"),
            resultSet.getString("arrival_station"),
            resultSet.getString("departure_date"),
            resultSet.getString("arrival_date"),
            resultSet.getString("departure_time"),
            resultSet.getString("arrival_time"),
            resultSet.getString("capacity"),
            resultSet.getString("status") // Include status in the constructor
        );
    }
        
    public static Schedule getScheduleByTrainName(String trainName) {
        Schedule schedule = null;
        String query = "SELECT s.id, s.train_name, s.route, s.departure_station, s.arrival_station, "
                     + "s.departure_date, s.arrival_date, s.departure_time, s.arrival_time, "
                     + "s.capacity, s.status "
                     + "FROM schedule s "
                     + "WHERE s.train_name = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, trainName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                schedule = createScheduleFromResultSet(rs);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error while fetching schedule by train name", e);
        }
        return schedule;
    }

}