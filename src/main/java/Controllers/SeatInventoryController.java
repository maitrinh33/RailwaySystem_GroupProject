package Controllers;

import Models.Seat;
import Utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeatInventoryController {
    private Connection connection;

    // Constructor to establish a database connection
    public SeatInventoryController(Connection connection) {
        this.connection = connection;
    }

    /**
     * Retrieves all seats for a specific train from the Seat database.
     *
     * @param trainId ID of the train whose seats to retrieve.
     * @return List of Seat objects.
     * @throws SQLException If a database error occurs.
     */
    public List<Seat> getSeatsByTrainId(String trainId) throws SQLException {
        List<Seat> seats = new ArrayList<>();
        String query = "SELECT * FROM Seat WHERE train_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, trainId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Seat seat = new Seat(
                            resultSet.getString("id"),
                            resultSet.getString("train_id"),
                            resultSet.getString("coach_number"),  
                            resultSet.getString("class_type"),
                            resultSet.getString("seat_number"),  
                            resultSet.getBoolean("is_available")
                    );
                    seats.add(seat);
                }
            }
        }
        return seats;
    }

    /**
     * Updates the availability of a seat in the database.
     *
     * @param seatId       ID of the seat to update.
     * @param isAvailable New availability status.
     * @throws SQLException If a database error occurs.
     */
    public void updateSeatAvailability(String seatId, boolean isAvailable) throws SQLException { 
        String query = "UPDATE Seat SET is_available = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setBoolean(1, isAvailable);
            statement.setString(2, seatId);  
            statement.executeUpdate();
        }
    }

    /**
     * Adds a new seat to the Seat database.
     *
     * @param seat Seat object containing seat details.
     * @throws SQLException If a database error occurs.
     */
    public void addSeat(Seat seat) throws SQLException {
        String query = "INSERT INTO Seat (train_id, coach_number, class_type, seat_number, is_available) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, seat.getTrainId());
            statement.setString(2, seat.getCoachNumber());  // Changed to String for coachNumber
            statement.setString(3, seat.getClassType());
            statement.setString(4, seat.getSeatNumber());  // Changed to String for seatNumber
            statement.setBoolean(5, seat.isAvailable());
            statement.executeUpdate();
        }
    }

    /**
     * Deletes a seat from the Seat database.
     *
     * @param seatId ID of the seat to delete.
     * @throws SQLException If a database error occurs.
     */
    public void deleteSeat(String seatId) throws SQLException {  // Changed seatId to String
        String query = "DELETE FROM Seat WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, seatId);  // Changed to String for seatId
            statement.executeUpdate();
        }
    }

    void setTrainId(String id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
