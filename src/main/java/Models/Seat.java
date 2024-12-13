package Models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

import java.util.Collections;
import java.util.List;

public class Seat {
private final String id; // Unique identifier for the seat
private final String trainId; // ID of the train this seat belongs to
private final StringProperty classType; // Class type (e.g., Sleeper, AC)
private final IntegerProperty coachNumber; // Coach number
private final IntegerProperty totalSeats; // Total number of seats in the coach
private final IntegerProperty availableSeats; // Number of available seats
private final List<String> seatIds; // List of individual seat IDs

// Constructor
public Seat(String id, String trainId, int coachNumber, String classType, 
            int totalSeats, int availableSeats, List<String> seatIds) {
    this.id = id;
    this.trainId = trainId;
    this.coachNumber = new SimpleIntegerProperty(coachNumber);
    this.classType = new SimpleStringProperty(classType);
    this.totalSeats = new SimpleIntegerProperty(totalSeats);
    this.availableSeats = new SimpleIntegerProperty(availableSeats);
    this.seatIds = seatIds != null ? seatIds : Collections.emptyList(); // Initialize to empty list if null
}

    // Getters
    public String getId() {
        return id;
    }

    public String getTrainId() {
        return trainId;
    }

    public int getCoachNumber() {
        return coachNumber.get();
    }

    public String getClassType() {
        return classType.get();
    }

    public int getTotalSeats() {
        return totalSeats.get();
    }

    public int getAvailableSeats() {
        return availableSeats.get();
    }

    public List<String> getSeatIds() {
        return Collections.unmodifiableList(seatIds); // Return an unmodifiable list
    }

    // Properties for JavaFX binding
    public IntegerProperty coachNumberProperty() {
        return coachNumber;
    }

    public StringProperty classTypeProperty() {
        return classType;
    }

    public IntegerProperty totalSeatsProperty() {
        return totalSeats;
    }

    public IntegerProperty availableSeatsProperty() {
        return availableSeats;
    }
}