package Models;

import java.sql.Timestamp;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

public class Reservation {
    private final IntegerProperty id;
    private final IntegerProperty scheduleId;
    private final StringProperty customerName;
    private final StringProperty customerPassport;
    private final IntegerProperty seatId;
    private final Timestamp reservationTime;
    private final StringProperty paymentStatus;
    private final StringProperty status;

    public Reservation(int id, int scheduleId, String customerName, String customerPassport,
                       int seatId, Timestamp reservationTime, String paymentStatus, String status) {
        this.id = new SimpleIntegerProperty(id);
        this.scheduleId = new SimpleIntegerProperty(scheduleId);
        this.customerName = new SimpleStringProperty(customerName);
        this.customerPassport = new SimpleStringProperty(customerPassport);
        this.seatId = new SimpleIntegerProperty(seatId);
        this.reservationTime = reservationTime;
        this.paymentStatus = new SimpleStringProperty(paymentStatus);
        this.status = new SimpleStringProperty(status);
    }

    // Getters and Setters for JavaFX Binding
    public IntegerProperty idProperty() {
        return id;
    }

    public IntegerProperty scheduleIdProperty() {
        return scheduleId;
    }

    public StringProperty customerNameProperty() {
        return customerName;
    }

    // Other getters and setters...
}