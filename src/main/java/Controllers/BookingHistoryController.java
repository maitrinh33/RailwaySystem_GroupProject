package Controllers;

import Models.Booking;
import Utils.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

public class BookingHistoryController {

    @FXML
    private TableView<Booking> bookingTable;
    @FXML
    private TextField ticketIdField;
    @FXML
    private TextField customerNameField;
    @FXML
    private TextField passportField;
    @FXML
    private DatePicker bookDatePicker;
    @FXML
    private ComboBox<String> statusComboBox;

    @FXML
    private void initialize() {
        TableColumn<Booking, Integer> bookingIdColumn = new TableColumn<>("Booking ID");
        bookingIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookingId"));

        TableColumn<Booking, String> ticketIdColumn = new TableColumn<>("Ticket ID");
        ticketIdColumn.setCellValueFactory(new PropertyValueFactory<>("ticketId"));

        TableColumn<Booking, String> customerNameColumn = new TableColumn<>("Customer Name");
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));

        TableColumn<Booking, String> passportColumn = new TableColumn<>("Passport");
        passportColumn.setCellValueFactory(new PropertyValueFactory<>("passport"));

        TableColumn<Booking, Double> paymentAmountColumn = new TableColumn<>("Payment");
        paymentAmountColumn.setCellValueFactory(new PropertyValueFactory<>("paymentAmount"));

        TableColumn<Booking, String> bookingTimeColumn = new TableColumn<>("Booking Time");
        bookingTimeColumn.setCellValueFactory(new PropertyValueFactory<>("bookingTime"));

        TableColumn<Booking, String> departureDateColumn = new TableColumn<>("Departure Date");
        departureDateColumn.setCellValueFactory(new PropertyValueFactory<>("departureDate"));

        TableColumn<Booking, String> trainNameColumn = new TableColumn<>("Train");
        trainNameColumn.setCellValueFactory(new PropertyValueFactory<>("trainName"));

        TableColumn<Booking, String> routeColumn = new TableColumn<>("Route");
        routeColumn.setCellValueFactory(new PropertyValueFactory<>("route"));

        TableColumn<Booking, String> classTypeColumn = new TableColumn<>("Class Type");
        classTypeColumn.setCellValueFactory(new PropertyValueFactory<>("classType"));

        TableColumn<Booking, String> seatNumberColumn = new TableColumn<>("Seat");
        seatNumberColumn.setCellValueFactory(new PropertyValueFactory<>("seatNumber"));

        TableColumn<Booking, String> coachNumberColumn = new TableColumn<>("Coach");
        coachNumberColumn.setCellValueFactory(new PropertyValueFactory<>("coachNumber"));

        TableColumn<Booking, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        statusColumn.setCellFactory(column -> new TableCell<Booking, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    switch (status) {
                        case "Confirmed" -> setTextFill(javafx.scene.paint.Color.LIGHTGREEN);
                        case "Returned" -> setTextFill(javafx.scene.paint.Color.RED);
                        case "Cancelled" -> setTextFill(javafx.scene.paint.Color.ORANGE);
                        default -> setTextFill(javafx.scene.paint.Color.BLACK); // Default color
                    }
                }
            }
        });

        bookingTable.getColumns().addAll(
                bookingIdColumn, ticketIdColumn,customerNameColumn, passportColumn, 
                paymentAmountColumn, bookingTimeColumn, departureDateColumn, trainNameColumn, 
                routeColumn, classTypeColumn, seatNumberColumn, coachNumberColumn, statusColumn
        );

        statusComboBox.getItems().addAll("All", "Paid", "Cancelled");
        statusComboBox.setValue("All");  
        loadBookings();
    }

    private void loadBookings() {
        ObservableList<Booking> bookings = fetchBookingsFromDatabase(null, null, null, null, null);
        bookingTable.setItems(bookings);
    }

    @FXML
    private void handleFindButton() {
        String ticketId = ticketIdField.getText().trim();
        String customerName = customerNameField.getText().trim();
        String passport = passportField.getText().trim();
        String status = statusComboBox.getValue();
        LocalDate departureDate = bookDatePicker.getValue();

        ObservableList<Booking> filteredBookings = fetchBookingsFromDatabase(ticketId, customerName, passport, status, departureDate);
        bookingTable.setItems(filteredBookings);
    }

    private ObservableList<Booking> fetchBookingsFromDatabase(String ticketId, String customerName, String passport, String status, LocalDate departureDate) {
        ObservableList<Booking> bookings = FXCollections.observableArrayList();

        String query = "SELECT * FROM booking WHERE 1=1";

        if (ticketId != null && !ticketId.isEmpty()) {
            query += " AND ticket_id LIKE '%" + ticketId + "%'";
        }
        if (customerName != null && !customerName.isEmpty()) {
            query += " AND customer_name LIKE '%" + customerName + "%'";
        }
        if (passport != null && !passport.isEmpty()) {
            query += " AND passport LIKE '%" + passport + "%'";
        }
        if (status != null && !status.equals("All")) {
            query += " AND status = '" + status + "'";
        }
        if (departureDate != null) {
            query += " AND departure_date = '" + departureDate + "'";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Booking booking = new Booking(
                        rs.getInt("booking_id"),
                        rs.getString("ticket_id"),
                        rs.getString("customer_name"),
                        rs.getString("passport"),
                        rs.getDouble("payment_amount"),
                        rs.getString("booking_time"),
                        rs.getString("departure_date"),
                        rs.getString("train_name"),
                        rs.getString("route"),
                        rs.getString("class_type"),
                        rs.getString("seat_number"),
                        rs.getString("coach_number"),
                        rs.getString("status")
                );
                bookings.add(booking);
            }
        } catch (SQLException e) {
        }
        return bookings;
    }

    @FXML
    private void handleClearButton(MouseEvent event) {
        ticketIdField.clear();
        customerNameField.clear();
        passportField.clear();
        bookDatePicker.setValue(null);
        statusComboBox.setValue("All"); 
        loadBookings();
    }
}
