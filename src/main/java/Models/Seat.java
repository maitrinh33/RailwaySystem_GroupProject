package Models;

import java.util.List;

public class Seat {
    private final String id;
    private final String trainId;
    private final int coachNumber;
    private final String classType;
    private final int totalSeats;
    private final int availableSeats;
    private List<String> seatIds; // Changed to List

    // Constructor
    public Seat(String id, String trainId, int coachNumber, String classType, int totalSeats, int availableSeats, List<String> seatIds) {
        this.id = id;
        this.trainId = trainId;
        this.coachNumber = coachNumber;
        this.classType = classType;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
        this.seatIds = seatIds; // Initialize the list
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public String getTrainId() {
        return trainId;
    }

    public int getCoachNumber() {
        return coachNumber;
    }

    public String getClassType() {
        return classType;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public List<String> getSeatIds() {
        return seatIds; // Return the list
    }

    public void setSeatIds(List<String> seatIds) {
        this.seatIds = seatIds; // Setter for seat IDs
    }
}