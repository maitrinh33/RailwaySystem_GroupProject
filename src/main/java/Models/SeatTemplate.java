package Models;

public class SeatTemplate {
    private final String name;
    private final int totalSeats;
    private final int availableSeats;

    public SeatTemplate(String name, int totalSeats, int availableSeats) {
        this.name = name;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
    }

    public String getName() {
        return name;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    @Override
    public String toString() {
        return name; 
    }
}