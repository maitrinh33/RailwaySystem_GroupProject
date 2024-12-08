package Models;

public class SeatTemplate {
    private String name;
    private int totalSeats;
    private int availableSeats;

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
        return name; // This will be shown in the ComboBox
    }
}