package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import Utils.DatabaseConnection;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class ReturnTicketController {

    @FXML
    private TextField ticketIdField;

    @FXML
    private Button searchButton;

    @FXML
    private RadioButton changeTicketOption;

    @FXML
    private RadioButton returnTicketOption;

    @FXML
    private Label customerName, passport, departureDate, route, trainName, coachNumber, classType, seatNumber, customerName7, payment;

    @FXML
    private TextField refundAmountField, feesField, totalRefundField;

    @FXML
    private Button uploadButton;

    @FXML
    private ImageView passportImageView;

    @FXML
    private Button returnButton;

    private File passportFile;

    @FXML
    public void initialize() {
        searchButton.setOnAction(this::handleSearch);
        uploadButton.setOnAction(this::handleUpload);
        returnButton.setOnAction(this::handleReturn);
        
        // Add listeners for radio buttons
        returnTicketOption.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // Show refund-related fields when "returnTicketOption" is selected
                refundAmountField.setVisible(true);
                feesField.setVisible(true);
                totalRefundField.setVisible(true);
            } else {
                // Hide refund-related fields when "returnTicketOption" is not selected
                refundAmountField.setVisible(false);
                feesField.setVisible(false);
                totalRefundField.setVisible(false);
            }
        });
        
        // Attach listeners for dynamic calculation
        refundAmountField.textProperty().addListener((observable, oldValue, newValue) -> calculateTotalRefund());
        feesField.textProperty().addListener((observable, oldValue, newValue) -> calculateTotalRefund());
        // Hide fields initially if needed
        refundAmountField.setVisible(false);
        feesField.setVisible(false);
        totalRefundField.setVisible(false);
    }

@FXML
private void handleSearch(ActionEvent event) {
    String ticketId = ticketIdField.getText();
    if (ticketId.isEmpty()) {
        showAlert(Alert.AlertType.WARNING, "Warning", "Please enter a Ticket ID.");
        return;
    }

    try (Connection connection = DatabaseConnection.getConnection()) {
        String query = "SELECT * FROM ticket t " +
                       "JOIN booking b ON t.ticket_id = b.ticket_id " +  // Match ticket_id in both tables
                       "WHERE t.ticket_id = ?";  // Use ticket_id here for comparison
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, ticketId); // Use setString for the ticket_id (VARCHAR)

        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            // Populate the other fields
            customerName.setText(resultSet.getString("customer_name"));
            passport.setText(resultSet.getString("passport"));
            departureDate.setText(resultSet.getString("departure_date"));
            route.setText(resultSet.getString("route"));
            trainName.setText(resultSet.getString("train_name"));
            coachNumber.setText(resultSet.getString("coach_number"));
            classType.setText(resultSet.getString("class_type"));
            seatNumber.setText(resultSet.getString("seat_id"));
            customerName7.setText(resultSet.getString("status"));
            payment.setText(resultSet.getString("payment_amount"));

            // Set the base fare in the refundAmountField
            double baseFare = resultSet.getDouble("base_fare");
            refundAmountField.setText(String.valueOf(baseFare));

            // Automatically show the refund fields when ticket is found and return is selected
            if (returnTicketOption.isSelected()) {
                refundAmountField.setVisible(true);
                feesField.setVisible(true);
                totalRefundField.setVisible(true);
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Ticket not found.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Error", "Database error occurred.");
    }
}

    private double processRefund(LocalDateTime departureDate, double fare) {
        LocalDateTime currentDateTime = LocalDateTime.now(ZoneId.systemDefault());
        long hoursDifference = java.time.Duration.between(currentDateTime, departureDate).toHours();

        if (hoursDifference > 24) {
            return fare; // Full refund
        } else if (hoursDifference <= 24 && hoursDifference > 0) {
            return fare * 0.7; // 70% refund
        }
        return 0; // No refund
    }

    private void handleUpload(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        passportFile = fileChooser.showOpenDialog(uploadButton.getScene().getWindow());
        if (passportFile != null) {
            passportImageView.setImage(new Image(passportFile.toURI().toString()));
            try {
                File destination = new File("uploaded_passports/" + passportFile.getName());
                Files.copy(passportFile.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
                passportFile = destination;
            } catch (IOException e) {
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Warning", "No file selected.");
        }
    }

    private void handleReturn(ActionEvent event) {
       if (passportFile == null) {
           showAlert(Alert.AlertType.WARNING, "Warning", "Please upload a passport picture.");
           return;
       }

       String ticketId = ticketIdField.getText();
       if (ticketId.isEmpty()) {
           showAlert(Alert.AlertType.WARNING, "Warning", "Please enter a Ticket ID.");
           return;
       }

       // Validate the ticket ID format (must start with "TKT-" and then only digits)
       if (!ticketId.matches("TKT-\\d+")) {
           showAlert(Alert.AlertType.ERROR, "Error", "Invalid Ticket ID format. It should start with 'TKT-' followed by numbers.");
           return;
       }

       try (Connection connection = DatabaseConnection.getConnection()) {
           // Check if the ticket has already been refunded
           String refundCheckQuery = "SELECT status FROM refund WHERE ticket_id = ?";
           PreparedStatement refundCheckStmt = connection.prepareStatement(refundCheckQuery);
           refundCheckStmt.setString(1, ticketId);
           ResultSet refundCheckResult = refundCheckStmt.executeQuery();

           if (refundCheckResult.next()) {
               String refundStatus = refundCheckResult.getString("status");
               if ("Refunded".equalsIgnoreCase(refundStatus) || "Returned".equalsIgnoreCase(refundStatus)) {
                   showAlert(Alert.AlertType.ERROR, "Error", "This ticket has already been returned or refunded.");
                   return;
               }
           }

           // Retrieve the booking ID for this ticket ID
           String bookingQuery = "SELECT booking_id FROM booking WHERE ticket_id = ?";
           PreparedStatement bookingStatement = connection.prepareStatement(bookingQuery);
           bookingStatement.setString(1, ticketId);
           ResultSet bookingResult = bookingStatement.executeQuery();

           if (bookingResult.next()) {
               int bookingId = bookingResult.getInt("booking_id");

               // Calculate the refund amount (final refund from user input)
               double refund = refundAmountField.getText().isEmpty() ? 0 : Double.parseDouble(refundAmountField.getText());
               double fees = feesField.getText().isEmpty() ? 0 : Double.parseDouble(feesField.getText());
               double finalRefund = refund - fees;

               if (finalRefund < 0) {
                   showAlert(Alert.AlertType.ERROR, "Error", "Refund amount cannot be negative.");
                   return;
               }

               // Insert refund data into the refund table
               String insertRefundQuery = "INSERT INTO refund (ticket_id, booking_id, refund_amount, status) " +
                                          "VALUES (?, ?, ?, ?)";
               PreparedStatement insertStatement = connection.prepareStatement(insertRefundQuery);
               insertStatement.setString(1, ticketId);
               insertStatement.setInt(2, bookingId);
               insertStatement.setDouble(3, finalRefund);
               insertStatement.setString(4, "Refunded");

               insertStatement.executeUpdate();

               // Display the final refund amount in the total refund field
               totalRefundField.setText(String.format("%.2f", finalRefund));
               showAlert(Alert.AlertType.INFORMATION, "Success", "Refund processed successfully.\nAmount: " + finalRefund);
           } else {
               showAlert(Alert.AlertType.ERROR, "Error", "Booking ID not found for this ticket.");
           }

       } catch (SQLException e) {
           e.printStackTrace();
           showAlert(Alert.AlertType.ERROR, "Error", "Database error occurred.");
       } catch (NumberFormatException e) {
           showAlert(Alert.AlertType.ERROR, "Error", "Invalid number format for refund or fees.");
       } catch (Exception e) {
           showAlert(Alert.AlertType.ERROR, "Error", "Unexpected error occurred.");
       }
   }


    private void calculateTotalRefund() {
    try {
        // Get the base fare and validate input
        double refund = refundAmountField.getText().isEmpty() ? 0 : Double.parseDouble(refundAmountField.getText());
        double fees = feesField.getText().isEmpty() ? 0 : Double.parseDouble(feesField.getText());

        // Ensure fees don't exceed refund
        if (fees > refund) {
            feesField.setText(String.format("%.2f", refund));
            fees = refund;
        }

        // Calculate and display the final refund
        double finalRefund = refund - fees;
        totalRefundField.setText(String.format("%.2f", finalRefund));
    } catch (NumberFormatException e) {
        totalRefundField.setText("0.0");
    }
}

    @FXML
    private void handleRefundAmountChange() {
        calculateTotalRefund();
    }

    @FXML
    private void handleFeesChange() {
        calculateTotalRefund();
    }


    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}