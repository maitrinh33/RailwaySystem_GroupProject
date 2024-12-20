package Models;

import Models.Seat;
import Models.Schedule;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class Ticket {
    private final SimpleStringProperty ticketId;
    private final SimpleObjectProperty<LocalDate> departureDate;
    private final SimpleObjectProperty<Schedule> schedule; // Reference to Schedule object
    private final SimpleObjectProperty<Seat> seat;         // Reference to Seat object
    private final SimpleStringProperty ticketType; // e.g., "Adult", "Student"
    private final SimpleDoubleProperty baseFare;
    private final SimpleDoubleProperty subsidyAmount;
    private final SimpleDoubleProperty totalFare;
    private final SimpleStringProperty customerName;
    private final SimpleStringProperty passport;
    private final SimpleObjectProperty<LocalDateTime> reservationTime; 
    private SimpleStringProperty timeRemaining = new SimpleStringProperty("10:00"); 
    private boolean isPaid; 
    // Constructor
    public Ticket(String ticketId, LocalDate departureDate, Schedule schedule, Seat seat,
                  String ticketType, double baseFare, double subsidyAmount, double totalFare,
                  String customerName, String passport, LocalDateTime reservationTime) { // Changed type here
        this.ticketId = new SimpleStringProperty(ticketId);
        this.departureDate = new SimpleObjectProperty<>(departureDate);
        this.schedule = new SimpleObjectProperty<>(schedule);
        this.seat = new SimpleObjectProperty<>(seat);
        this.ticketType = new SimpleStringProperty(ticketType);
        this.baseFare = new SimpleDoubleProperty(baseFare);
        this.subsidyAmount = new SimpleDoubleProperty(subsidyAmount);
        this.totalFare = new SimpleDoubleProperty(totalFare);
        this.customerName = new SimpleStringProperty(customerName);
        this.passport = new SimpleStringProperty(passport);
        this.reservationTime = new SimpleObjectProperty<>(reservationTime); // Changed to SimpleObjectProperty
    }

    // Getters for JavaFX properties
    public String getTicketId() {
        return ticketId.get();
    }

    public SimpleStringProperty ticketIdProperty() {
        return ticketId;
    }

    public LocalDate getDepartureDate() {
        return departureDate.get();
    }

    public SimpleObjectProperty<LocalDate> departureDateProperty() {
        return departureDate;
    }

    public Schedule getSchedule() {
        return schedule.get();
    }

    public SimpleObjectProperty<Schedule> scheduleProperty() {
        return schedule;
    }

    public Seat getSeat() {
        return seat.get();
    }

    public SimpleObjectProperty<Seat> seatProperty() {
        return seat;
    }

    public String getTicketType() {
        return ticketType.get();
    }

    public SimpleStringProperty ticketTypeProperty() {
        return ticketType;
    }

    public double getBaseFare() {
        return baseFare.get();
    }

    public SimpleDoubleProperty baseFareProperty() {
        return baseFare;
    }

    public double getSubsidyAmount() {
        return subsidyAmount.get();
    }

    public SimpleDoubleProperty subsidyAmountProperty() {
        return subsidyAmount;
    }

    public double getTotalFare() {
        return totalFare.get();
    }

    public SimpleDoubleProperty totalFareProperty() {
        return totalFare;
    }

    public String getCustomerName() {
        return customerName.get();
    }

    public SimpleStringProperty customerNameProperty() {
        return customerName;
    }

    public String getPassport() {
        return passport.get();
    }

    public SimpleStringProperty passportProperty() {
        return passport;
    }

    public LocalDateTime getReservationTime() {
        return reservationTime.get();
    }

    public SimpleObjectProperty<LocalDateTime> reservationTimeProperty() {
        return reservationTime;
    }
    
    public boolean isExpired() {
        return Duration.between(reservationTime.get(), LocalDateTime.now()).getSeconds() > 600; // 10 minutes
    }
    
    // Getter for isPaid
    public boolean isPaid() {
        return isPaid;
    }

    // Setter for isPaid (if needed)
    public void setPaid(boolean isPaid) {
        this.isPaid = isPaid;
    }
    
    public void updateTimeRemaining() {
        long secondsRemaining = 600 - Duration.between(reservationTime.get(), LocalDateTime.now()).getSeconds();

        if (secondsRemaining <= 0) {
            timeRemaining.set("Expired");
        } else {
            long minutes = secondsRemaining / 60;
            long seconds = secondsRemaining % 60;
            timeRemaining.set(String.format("%02d:%02d", minutes, seconds));
        }
    }

    public SimpleStringProperty timeRemainingProperty() {
        return timeRemaining;
    }

    public String getTimeRemaining() {
        return timeRemaining.get();
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "ticketId='" + ticketId.get() + '\'' +
                ", departureDate=" + departureDate.get() +
                ", schedule=" + (schedule.get() != null ? schedule.get().getTrainName() + " (" + schedule.get().getRoute() + ")" : "N/A") +
                ", seat=" + (seat.get() != null ? seat.get().getSeatNumber() + ", classType=" + seat.get().getClassType() : "N/A") +
                ", ticketType='" + ticketType.get() + '\'' +
                ", baseFare=" + baseFare.get() +
                ", subsidyAmount=" + subsidyAmount.get() +
                ", totalFare=" + totalFare.get() +
                ", customerName='" + customerName.get() + '\'' +
                ", passport='" + passport.get() + '\'' +
                ", reservationTime=" + reservationTime.get() +
                '}';
    }
}