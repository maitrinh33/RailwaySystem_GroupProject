package Controllers;

import Models.Schedule;
import static Models.Schedule.getScheduleByTrainName;
import Models.Seat;
import Models.Ticket;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration; 


public class BookTicketController {
    @FXML
    private Button btnOpenCheckTicket; 
    
    @FXML
    private DatePicker departureDatePicker; 

    @FXML
    private ComboBox<String> trainName; 

    @FXML
    private ComboBox<String> departureStation; 

    @FXML
    private ComboBox<String> arrivalStation; 

    @FXML
    private ComboBox<String> coach; 

    @FXML
    private ComboBox<String> classType; 

    @FXML
    private ComboBox<String> seat; 
    
    @FXML
    private ComboBox<String> ticketTypeComboBox; 
    
    @FXML
    private TextField customerNameField; 
    
    @FXML
    private TextField passportField; 
    
    @FXML
    private TextField totalTicketsField; 

    @FXML
    private TextField totalMoneyField; 
    
    @FXML
    private TextField cashField;
    
    @FXML
    private TextField changeField;
    
    
    @FXML
    private TableView<Ticket> ticketTableView; 
    
    @FXML
    private Button deleteButton;
    private Ticket selectedTicket; 

    private final ObservableList<Ticket> ticketList = FXCollections.observableArrayList(); 
        private SidebarController sidebarController;

    private static final Logger logger = Logger.getLogger(CheckTicketController.class.getName());

    private static final String DB_URL = "jdbc:mysql://localhost:3306/railway_system";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "3005";

    public void setSidebarController(SidebarController sidebarController) {
        this.sidebarController = sidebarController;
    }
    
    public void openCheckTicket(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CheckTicket.fxml"));
        AnchorPane root = loader.load();

        CheckTicketController checkTicketController = loader.getController();
        checkTicketController.setBookTicketController(this);
        checkTicketController.setOpenedFromSidebar(false); // Indicate opened as standalone

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Check Ticket Availability");
        stage.show();
    }

    public void loadBookTicketPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/BookTicket.fxml"));
            AnchorPane newContent = loader.load();

            // Wrap the content in a ScrollPane to make it scrollable
            ScrollPane scrollPane = new ScrollPane(newContent);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);

            // Assuming you have a reference to the main pane in sidebarController
            if (sidebarController != null) {
                sidebarController.getMainPane().setCenter(scrollPane);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error loading BookTicket view", e);
            showAlert("An error occurred while loading the BookTicket view.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.showAndWait();
    }
    
    public void initialize() {
            setupTableColumns();
            setupTicketTypeComboBox();  
            removeExpiredTickets();
            releaseExpiredSeats();
            setupListeners();
                // Disable the delete button initially
                deleteButton.setDisable(true);

                // Enable the delete button if a ticket is selected
                ticketTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                    deleteButton.setDisable(newSelection == null); 
                });
    }

    private void setupListeners() {
        ticketTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) { // Single-click to select
                selectedTicket = ticketTableView.getSelectionModel().getSelectedItem();
            }
        });
    }
    
    @FXML
    private void handleDeleteButtonAction() {
        if (selectedTicket != null) {
            deleteTicket(selectedTicket);
            selectedTicket = null; // Clear selection after deletion
        }
    }
    
    private void setupTicketTypeComboBox() {
        // Define options for the ComboBox
        ObservableList<String> ticketTypes = FXCollections.observableArrayList("Adult", "Children", "Student");

        // Set items in the ComboBox
        ticketTypeComboBox.setItems(ticketTypes);

        // Add listener for selection changes
        ticketTypeComboBox.setOnAction(event -> {
            String selectedType = ticketTypeComboBox.getValue();
            System.out.println("Selected Ticket Type: " + selectedType);

            // Add logic based on the selection if needed
        });
    }
    
    public void fillTicketDetails(String date, String trainName, String departureStation, String arrivalStation,
                                   String coachNumber, String classType, String seatNumber) {
        if (departureDatePicker != null) {
            departureDatePicker.setValue(LocalDate.parse(date));
        }
        if (this.trainName != null) {
            this.trainName.setValue(trainName);
        }
        if (this.departureStation != null) {
            this.departureStation.setValue(departureStation);
        }
        if (this.arrivalStation != null) {
            this.arrivalStation.setValue(arrivalStation);
        }
        if (this.coach != null) {
            this.coach.setValue(coachNumber);
        }
        if (this.classType != null) {
            this.classType.setValue(classType);
        }
        if (this.seat != null) {
            seat.getItems().clear();
            seat.getItems().add(seatNumber);
            seat.setValue(seatNumber);
        }
    }
    
    private void setupTableColumns() {
        ticketTableView.getColumns().clear();

        // Ticket ID Column
        TableColumn<Ticket, String> ticketIdCol = new TableColumn<>("Ticket ID");
        ticketIdCol.setCellValueFactory(new PropertyValueFactory<>("ticketId"));

        // Departure Date Column
        TableColumn<Ticket, LocalDate> departureDateCol = new TableColumn<>("Departure Date");
        departureDateCol.setCellValueFactory(new PropertyValueFactory<>("departureDate"));

        // Train Name Column
        TableColumn<Ticket, String> trainNameCol = new TableColumn<>("Train Name");
        trainNameCol.setCellValueFactory(cellData -> {
            Schedule schedule = cellData.getValue().getSchedule();
            return schedule != null ? schedule.trainNameProperty() : new SimpleStringProperty("N/A");
        });

        // Route Column
        TableColumn<Ticket, String> routeCol = new TableColumn<>("Route");
        routeCol.setCellValueFactory(cellData -> {
            Schedule schedule = cellData.getValue().getSchedule();
            return schedule != null ? schedule.routeProperty() : new SimpleStringProperty("N/A");
        });

        // Seat Number Column
        TableColumn<Ticket, String> seatNumberCol = new TableColumn<>("Seat");
        seatNumberCol.setCellValueFactory(cellData -> {
            Seat seat = cellData.getValue().getSeat();
            return seat != null ? seat.seatNumberProperty() : new SimpleStringProperty("N/A");
        });

        // Class Type Column
        TableColumn<Ticket, String> classTypeCol = new TableColumn<>("Class Type");
        classTypeCol.setCellValueFactory(cellData -> {
            Seat seat = cellData.getValue().getSeat();
            return seat != null ? seat.classTypeProperty() : new SimpleStringProperty("N/A");
        });

        // Ticket Type Column
        TableColumn<Ticket, String> ticketTypeCol = new TableColumn<>("Ticket Type");
        ticketTypeCol.setCellValueFactory(new PropertyValueFactory<>("ticketType"));

        // Base Fare Column
        TableColumn<Ticket, Double> baseFareCol = new TableColumn<>("Base Fare");
        baseFareCol.setCellValueFactory(new PropertyValueFactory<>("baseFare"));

        // Subsidy Amount Column
        TableColumn<Ticket, Double> subsidyCol = new TableColumn<>("Subsidy Amount");
        subsidyCol.setCellValueFactory(new PropertyValueFactory<>("subsidyAmount"));

        // Total Fare Column
        TableColumn<Ticket, Double> totalFareCol = new TableColumn<>("Total Fare");
        totalFareCol.setCellValueFactory(new PropertyValueFactory<>("totalFare"));

        // Customer Name Column
        TableColumn<Ticket, String> customerNameCol = new TableColumn<>("Customer Name");
        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));

        // Passport Column
        TableColumn<Ticket, String> passportCol = new TableColumn<>("Passport");
        passportCol.setCellValueFactory(new PropertyValueFactory<>("passport"));

        // Reservation Time column (acting as Countdown)
        TableColumn<Ticket, String> reservationTimeCol = new TableColumn<>("Time Remaining");
        reservationTimeCol.setCellValueFactory(cellData -> cellData.getValue().timeRemainingProperty());

        // Timeline for countdown
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(10), event -> {
            ticketList.forEach(ticket -> {
                if (!ticket.isPaid()) {
                    // Update countdown display for unpaid tickets
                    ticket.updateTimeRemaining();
                }
            });
            ticketTableView.refresh(); // Refresh the view to show updated countdowns
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // Add all columns to the TableView
        ticketTableView.getColumns().addAll(
            ticketIdCol, departureDateCol, trainNameCol, routeCol, seatNumberCol, 
            classTypeCol, ticketTypeCol, baseFareCol, subsidyCol, totalFareCol,
            customerNameCol, passportCol, reservationTimeCol
        );

        // Set the items in the TableView
        ticketTableView.setItems(ticketList);
        
        // After adding the ticket, update totals
        updateTotals();
    }
    
    private void clearInputFields() {
        departureDatePicker.setValue(null);
        trainName.setValue(null);
        departureStation.setValue(null);
        arrivalStation.setValue(null);
        coach.setValue(null);
        classType.setValue(null);
        seat.setValue(null);
        ticketTypeComboBox.setValue(null);
        customerNameField.clear();
        passportField.clear();
    }
    
    private void refreshUI() {
        loadTicketsFromDatabase(); // Reload tickets to reflect any changes
        updateTotals(); // Update total tickets and money fields
    }
    
    @FXML
    public void addTicket(ActionEvent event) {
        // Gather input values
        String ticketId = generateTicketId();
        LocalDate departureDate = departureDatePicker.getValue();
        String selectedTrainName = trainName.getValue();
        String selectedDepartureStation = departureStation.getValue();
        String selectedArrivalStation = arrivalStation.getValue();
        String selectedCoach = coach.getValue();
        String selectedClassType = classType.getValue();
        String selectedSeat = seat.getValue();
        String ticketType = ticketTypeComboBox.getValue();
        String customerName = customerNameField.getText(); 
        String passport = passportField.getText();          
        String selectedticketType = ticketTypeComboBox.getValue(); 
        
        // Check if the departure date is null
        if (departureDate == null) {
            showAlert("Error", "Departure date must be selected.", Alert.AlertType.ERROR);
            return;
        }

        // Validate inputs
        if (selectedTrainName == null || selectedTrainName.isEmpty()) {
            showAlert("Error", "Train name must be selected.", Alert.AlertType.ERROR);
            return;
        }

        if (selectedCoach == null || !selectedCoach.matches("\\d+")) {
            showAlert("Error", "Please select a valid numeric coach number.", Alert.AlertType.ERROR);
            return;
        }

        if (customerName == null || customerName.isEmpty()) {
            showAlert("Error", "Customer name cannot be empty.", Alert.AlertType.ERROR);
            return;
        }

        if (passport == null || passport.isEmpty()) {
            showAlert("Error", "Passport number cannot be empty.", Alert.AlertType.ERROR);
            return;
        }

        if (selectedticketType == null || ticketType.isEmpty()) {
            showAlert("Error", "Ticket type cannot be empty.", Alert.AlertType.ERROR);
            return;
        }

        // Calculate fares
        double baseFare = getBaseFare(selectedTrainName, selectedCoach, selectedClassType);
        double subsidyPercentage = getSubsidyAmount(ticketType); // Get the subsidy percentage
        double subsidyAmount = baseFare * subsidyPercentage; // Calculate the subsidy amount
        double totalFare = baseFare - subsidyAmount; // Calculate the total fare

        // Fetch schedule_id and seat_id
        int scheduleId = getScheduleId(selectedTrainName);
        int seatId = getSeatId(selectedSeat, selectedTrainName, selectedCoach, selectedClassType);

        // Prepare and execute SQL insert
        String insertTicketSQL = "INSERT INTO ticket (ticket_id, departure_date, train_name, route, coach_seat, " +
                                 "class_type, ticket_type, base_fare, subsidy_amount, total_fare, customer_name, passport, " +
                                 "reservation_time, schedule_id, seat_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(insertTicketSQL)) {

            // Set parameters for the prepared statement
            stmt.setString(1, ticketId);
            stmt.setDate(2, Date.valueOf(departureDate));
            stmt.setString(3, selectedTrainName);
            stmt.setString(4, selectedDepartureStation + " - " + selectedArrivalStation);
            stmt.setString(5, selectedSeat);
            stmt.setString(6, selectedClassType);
            stmt.setString(7, ticketType);
            stmt.setDouble(8, baseFare);
            stmt.setDouble(9, subsidyAmount);
            stmt.setDouble(10, totalFare);
            stmt.setString(11, customerName);
            stmt.setString(12, passport);
            stmt.setTimestamp(13, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(14, scheduleId);
            stmt.setInt(15, seatId);

            // Execute the insert
            stmt.executeUpdate();
            
            // After successfully adding the ticket, mark the seat as unavailable
            markSeatAsUnavailable(selectedSeat, selectedTrainName);

            // Refresh the table view
            refreshUI();
            ticketTableView.setItems(ticketList); 
            setupTableColumns(); 
            showAlert("Success", "Ticket added successfully.", Alert.AlertType.INFORMATION);
            clearInputFields();
            
        } catch (SQLException e) {
            showAlert("Error", "Failed to add ticket: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void loadTicketsFromDatabase() {
        ticketList.clear(); 

        String sql = """
            SELECT 
                t.id AS id,
                t.ticket_id AS ticketIdentifier,
                t.departure_date,
                t.train_name,
                t.route,
                s.coach_number,
                s.seat_number,
                t.class_type,
                t.ticket_type,
                t.base_fare,
                t.subsidy_amount,
                t.total_fare,
                t.customer_name,
                t.passport,
                t.reservation_time,
                t.is_paid AS isPaid  
            FROM 
                ticket t
            JOIN 
                seat s ON t.seat_id = s.id
            JOIN 
                schedule sc ON t.schedule_id = sc.id
            WHERE 
                t.is_paid = FALSE  
        """;

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            
                        // Debugging: Print values being retrieved
            String trainName = rs.getString("train_name");
            String route = rs.getString("route");
            String seatNumber = rs.getString("seat_number");
            String classType = rs.getString("class_type");
            String coachNumber = rs.getString("coach_number");

            System.out.printf("Train Name: %s, Route: %s, Seat Number: %s, Class Type: %s, Coach Number: %s%n",
                trainName, route, seatNumber, classType, coachNumber);
            // Use the simplified Schedule constructor
            Schedule schedule = new Schedule(
                rs.getString("train_name"), // Train Name
                rs.getString("route") // Route
            );

            // Use the simplified Seat constructor
            Seat seat = new Seat(
                rs.getString("seat_number"), // Seat Number
                rs.getString("class_type"), // Class Type
                rs.getString("coach_number") // Coach Number
            );

            // Create the Ticket object
            Ticket ticket = new Ticket(
                rs.getString("ticketIdentifier"), // Ticket Identifier
                rs.getDate("departure_date").toLocalDate(), // Departure Date
                schedule, // Simplified Schedule Object
                seat, // Simplified Seat Object
                rs.getString("ticket_type"), // Ticket Type
                rs.getDouble("base_fare"), // Base Fare
                rs.getDouble("subsidy_amount"), // Subsidy Amount
                rs.getDouble("total_fare"), // Total Fare
                rs.getString("customer_name"), // Customer Name
                rs.getString("passport"), // Passport
                rs.getTimestamp("reservation_time").toLocalDateTime() // Reservation Time
            );
            
            // Set the isPaid status
            ticket.setPaid(rs.getBoolean("isPaid"));
            // Add the Ticket to the observable list
            ticketList.add(ticket); 
        }
        
        removeExpiredTickets();
        ticketTableView.setItems(ticketList); // Bind data to the table

        } catch (SQLException e) {
            showAlert("Error", "Failed to load tickets from database: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
        private void updateTotals() {
        int totalTickets = 0;
        double totalMoney = 0.0;

        for (Ticket ticket : ticketList) {
            if (!ticket.isExpired() && !ticket.isPaid()) { // Check for expiration and paid status
                totalTickets++;
                totalMoney += ticket.getTotalFare(); // Sum total fare
            }
        }

        // Update fields with totals
        totalTicketsField.setText(String.valueOf(totalTickets));
        Locale locale = Locale.GERMANY;
        NumberFormat format = NumberFormat.getInstance(locale);
        String formattedTotalMoney = format.format(totalMoney);
        totalMoneyField.setText(formattedTotalMoney);
    }


    @FXML
    public void handlePayment(ActionEvent event) {
        try {
            // Set locale to Germany to handle comma as decimal separator
            Locale locale = Locale.GERMANY;
            NumberFormat format = NumberFormat.getInstance(locale);

            // Parse the cash input
            double cash = format.parse(cashField.getText().trim()).doubleValue();
            double totalMoney = 0.0;

            // Collect unpaid tickets that are not expired
            List<Ticket> unpaidTickets = new ArrayList<>();
            for (Ticket ticket : ticketList) {
                if (!ticket.isPaid() && !ticket.isExpired()) {
                    unpaidTickets.add(ticket);
                    totalMoney += ticket.getTotalFare(); // Calculate total fare for unpaid tickets
                }
            }

            // Validate if cash is less than total money
            if (cash < totalMoney) {
                showAlert("Error", "Insufficient cash! Please provide enough cash for the total amount.", Alert.AlertType.ERROR);
                return;
            }

            double change = cash - totalMoney;

            // Save payment data
            int paymentId = savePayment(totalMoney, cash, change);
            logger.log(Level.INFO, "Payment ID: {0}", paymentId);

            if (paymentId != -1) {
                // Process each unpaid ticket
                for (Ticket ticket : unpaidTickets) {
                    LocalDate departureDate = ticket.getDepartureDate();
                    saveBookingData(ticket, departureDate);
                    markSeatAsUnavailable(ticket.getSeat().getSeatNumber(), ticket.getSchedule().getTrainName());
                    System.out.println("Marking Seat as Unavailable: " + ticket.getSeat().getSeatNumber());
                    markTicketAsPaid(ticket.getTicketId());
                }

                showAlert("Success", "Booking successful! Thank you for your purchase.", Alert.AlertType.INFORMATION);

                // Reload tickets to update UI
                loadTicketsFromDatabase();

                // Update totals
                updateTotals();

                // Reset fields
                totalTicketsField.setText("0");
                totalMoneyField.setText("0.00");
                cashField.clear();
                changeField.setText(String.format(locale, "%.2f", change));
            } else {
                showAlert("Error", "Failed to process payment. Please try again.", Alert.AlertType.ERROR);
            }
        } catch (ParseException e) {
            showAlert("Error", "An unexpected error occurred: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
private int getSeatId(String selectedSeat, String selectedTrainName, String selectedCoach, String selectedClassType) {
    int seatId = -1;
    String query = "SELECT id FROM seat WHERE seat_number = ? AND coach_number = ? AND class_type = ? AND train_id IN (" +
                   "SELECT id FROM schedule WHERE train_name = ?)";
    
    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
         PreparedStatement stmt = conn.prepareStatement(query)) {
        
        stmt.setString(1, selectedSeat);
        stmt.setString(2, selectedCoach); // Set as String directly
        stmt.setString(3, selectedClassType);
        stmt.setString(4, selectedTrainName);

        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            seatId = rs.getInt("id");
        } else {
            System.out.println("No seat found for the given parameters.");
        }
    } catch (SQLException e) {
        System.err.println("SQL Error: " + e.getMessage());
    } catch (IllegalArgumentException e) {
        System.err.println("Input Error: " + e.getMessage());
    }
    
    return seatId;
}

    private int getScheduleId(String trainName) {
        int scheduleId = -1;
        String query = "SELECT id FROM schedule WHERE train_name = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, trainName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                scheduleId = rs.getInt("id");
            }
        } catch (SQLException e) {
        }
        return scheduleId;
    }

    private double getBaseFare(String trainName, String coachNumber, String classType) {
        double baseFare = 0.0;
        String query = "SELECT base_fare FROM fare WHERE train_name = ? AND coach_number = ? AND class_type = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, trainName);
            stmt.setInt(2, Integer.parseInt(coachNumber)); // Ensure coach is parsed to int
            stmt.setString(3, classType);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                baseFare = rs.getDouble("base_fare");
                System.out.printf("Base fare found: %.2f%n", baseFare); // Debugging output
            } else {
                System.out.println("No fare found for Train: " + trainName + ", Coach: " + coachNumber + ", Class: " + classType);
            }
        } catch (SQLException e) {
        }
        return baseFare;
    }

    private double getSubsidyAmount(String ticketType) {
        double subsidyPercentage = 0.0;
        String query = "SELECT amount FROM subsidy WHERE type = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, ticketType);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                subsidyPercentage = rs.getDouble("amount");
            }
        } catch (SQLException e) {
        }
        return subsidyPercentage;
    }

    private String generateTicketId() {
        return "TKT-" + System.currentTimeMillis();
    }
    
    private void showAlert(String title, String message, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }
      

    // Method to save payment data
    private int savePayment(double totalMoney, double cashReceived, double changeReturned) {
        String insertPaymentSQL = "INSERT INTO payment (ticket_id, total_money, cash_received, change_returned) VALUES (?, ?, ?, ?)";
        int paymentId = -1; // Default to -1 to indicate failure

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(insertPaymentSQL, Statement.RETURN_GENERATED_KEYS)) {

            // Assuming you have access to the latest ticket ID
            Ticket lastTicket = ticketList.get(ticketList.size() - 1); // Get the last ticket added
            stmt.setString(1, lastTicket.getTicketId()); // Assuming getId() retrieves the ticket ID
            stmt.setDouble(2, totalMoney);
            stmt.setDouble(3, cashReceived);
            stmt.setDouble(4, changeReturned);

            // Execute the insert
            stmt.executeUpdate();

            // Retrieve generated payment ID
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                paymentId = generatedKeys.getInt(1); // Get generated payment ID
            }

        } catch (SQLException e) {
            showAlert("Error", "Failed to save payment: " + e.getMessage(), Alert.AlertType.ERROR);
        }

        return paymentId;
    }

    // Method to save booking data
    private void saveBookingData(Ticket lastTicket, LocalDate departureDate) {
        String insertBookingSQL = "INSERT INTO booking (ticket_id, customer_name, passport, payment_amount, booking_time, departure_date, train_name, seat_id, route, class_type, coach_number, seat_number) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(insertBookingSQL)) {


            // Save booking information
            stmt.setString(1, lastTicket.getTicketId()); // Use the ticket ID
            stmt.setString(2, lastTicket.getCustomerName()); // Customer name
            stmt.setString(3, lastTicket.getPassport()); // Passport
            stmt.setDouble(4, Double.parseDouble(totalMoneyField.getText())); // Ensure this field is valid
            stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now())); // Booking time
            stmt.setDate(6, Date.valueOf(departureDate)); // Departure date
            stmt.setString(7, lastTicket.getSchedule().getTrainName());
            stmt.setString(8, lastTicket.getSeat().getId()); // Seat ID
            stmt.setString(9, lastTicket.getSchedule().getRoute()); // Route
            stmt.setString(10, lastTicket.getSeat().getClassType()); // Class type
            stmt.setString(11, lastTicket.getSeat().getCoachNumber()); // Seat number
            stmt.setString(12, lastTicket.getSeat().getSeatNumber()); // Coach number
            // Execute the insert
            stmt.executeUpdate();

            // Mark the seat as unavailable
            markSeatAsUnavailable(lastTicket.getSeat().getSeatNumber(), lastTicket.getSchedule().getTrainName());

        } catch (SQLException e) {
        }
    }
    
    private void markSeatAsAvailable(String seatNumber, String coachNumber, String trainName) {
        String updateSeatSQL = "UPDATE seat SET is_available = true WHERE seat_number = ? AND coach_number = ? AND train_id IN (SELECT id FROM schedule WHERE train_name = ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(updateSeatSQL)) {
            stmt.setString(1, seatNumber);
            stmt.setString(2, coachNumber);
            stmt.setString(3, trainName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            showAlert("Error", "Failed to mark seat as available: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
        
    private void markSeatAsUnavailable(String seatNumber, String trainName) {
        String updateSeatSQL = "UPDATE seat SET is_available = false WHERE seat_number = ? AND train_id IN (SELECT id FROM schedule WHERE train_name = ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(updateSeatSQL)) {
            stmt.setString(1, seatNumber);
            stmt.setString(2, trainName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            showAlert("Error", "Failed to mark seat as unavailable: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void releaseExpiredSeats() {
        String sql = "UPDATE seat SET is_available = TRUE WHERE seat_number IN " +
                     "(SELECT seat_number FROM ticket WHERE reservation_time < NOW() - INTERVAL '10 MINUTE' AND is_paid = FALSE)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
            System.out.println("Expired seats have been released.");
        } catch (SQLException e) {
        }
    }

    private void removeExpiredTickets() {
        synchronized (ticketList) {
            List<Ticket> expiredTickets = ticketList.stream()
                .filter(Ticket::isExpired)
                .toList(); // Collect expired tickets

            expiredTickets.forEach(ticket -> deleteTicketFromDatabase(ticket)); // Remove from DB
            ticketList.removeAll(expiredTickets); // Update the observable list directly
        }
    }

       
    private void markTicketAsPaid(String ticketId) {
        String updateTicketSQL = "UPDATE ticket SET is_paid = TRUE WHERE ticket_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(updateTicketSQL)) {
            stmt.setString(1, ticketId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Ticket marked as paid: " + ticketId);
            } else {
                System.out.println("No ticket found with ID: " + ticketId);
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to update ticket status: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void deleteTicket(Ticket ticket) {
        // Confirm deletion with the user
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this ticket?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Ensure the ticket has an associated seat
            if (ticket.getSeat() != null) {
                // Retrieve seat details from the Ticket object
                String seatNumber = ticket.getSeat().getSeatNumber();
                String coachNumber = ticket.getSeat().getCoachNumber(); // Assuming this method exists
                String trainName = ticket.getSchedule().getTrainName(); // Assuming this method exists

                // Mark the seat as available
                markSeatAsAvailable(seatNumber, coachNumber, trainName);

                // Now delete the ticket from the database
                deleteTicketFromDatabase(ticket);
                showAlert("Success", "Ticket deleted successfully.", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Error", "No associated seat found for this ticket.", Alert.AlertType.ERROR);
            }
        }
    }
    
    private void deleteTicketFromDatabase(Ticket ticket) {
        String deleteTicketSQL = "DELETE FROM ticket WHERE ticket_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(deleteTicketSQL)) {
            stmt.setString(1, ticket.getTicketId());
            stmt.executeUpdate();

            // Remove the ticket from the table view
            ticketTableView.getItems().remove(ticket);
        } catch (SQLException e) {
            showAlert("Error", "Failed to delete ticket: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

}