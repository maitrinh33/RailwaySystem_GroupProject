package Models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Seat {
    private final String id; // Unique identifier for the seat (e.g., A1, B2)
    private String trainId; // ID of the train this seat belongs to
    private final StringProperty coachNumber; // Coach number
    private final StringProperty classType; // Class type (e.g., Sleeper, AC)
    private final StringProperty seatNumber; // Seat number (e.g., 1, 2, 3)
    private final BooleanProperty isAvailable; // Availability status of the individual seat

    // Constructor
    public Seat(String id, String trainId, String coachNumber, String classType, String seatNumber, boolean isAvailable) {
        this.id = id;
        this.trainId = trainId;
        this.coachNumber = new SimpleStringProperty(coachNumber);
        this.classType = new SimpleStringProperty(classType);
        this.seatNumber = new SimpleStringProperty(seatNumber);
        this.isAvailable = new SimpleBooleanProperty(isAvailable);
    }

    // Simplified Constructor for Tickets
    public Seat(String seatNumber, String classType, String coachNumber) {
        this.id = null; // Not needed for tickets
        this.trainId = null; // Not needed for tickets
        this.coachNumber = new SimpleStringProperty(coachNumber);
        this.classType = new SimpleStringProperty(classType);
        this.seatNumber = new SimpleStringProperty(seatNumber);
        this.isAvailable = new SimpleBooleanProperty(true); // Default
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getTrainId() {
        return trainId;
    }

    public String getCoachNumber() {
        return coachNumber.get();
    }

    public String getClassType() {
        return classType.get();
    }

    public String getSeatNumber() {
        return seatNumber.get();
    }

    public boolean isAvailable() {
        return isAvailable.get();
    }

    // Setters
    public void setAvailable(boolean available) {
        isAvailable.set(available);
    }

    public void setTrainId(String trainId) {
        this.trainId = trainId;
    }

    // Properties for JavaFX binding
    public StringProperty coachNumberProperty() {
        return coachNumber;
    }

    public StringProperty classTypeProperty() {
        return classType;
    }

    public StringProperty seatNumberProperty() {
        return seatNumber;
    }

    public BooleanProperty isAvailableProperty() {
        return isAvailable;
    }

    // Additional helper methods
    public boolean isSeatAvailable() {
        return isAvailable.get();
    }

    @Override
    public String toString() {
        return "Seat{" +
                "id=" + id +
                ", trainId='" + trainId + '\'' +
                ", coachNumber='" + coachNumber.get() + '\'' +
                ", classType='" + classType.get() + '\'' +
                ", seatNumber='" + seatNumber.get() + '\'' +
                ", isAvailable=" + isAvailable.get() +
                '}';
    }
}
