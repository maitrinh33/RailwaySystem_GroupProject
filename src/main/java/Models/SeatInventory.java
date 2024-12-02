/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Models;

import javafx.beans.property.*;

import java.time.LocalDate;

public class SeatInventory {

    private final IntegerProperty trainId;
    private final ObjectProperty<LocalDate> travelDate;
    private final StringProperty classType;
    private final IntegerProperty totalSeats;
    private final IntegerProperty availableSeats;

    public SeatInventory(int trainId, LocalDate travelDate, String classType, int totalSeats, int availableSeats) {
        this.trainId = new SimpleIntegerProperty(trainId);
        this.travelDate = new SimpleObjectProperty<>(travelDate);
        this.classType = new SimpleStringProperty(classType);
        this.totalSeats = new SimpleIntegerProperty(totalSeats);
        this.availableSeats = new SimpleIntegerProperty(availableSeats);
    }

    // Getters
    public int getTrainId() {
        return trainId.get();
    }

    public IntegerProperty trainIdProperty() {
        return trainId;
    }

    public LocalDate getTravelDate() {
        return travelDate.get();
    }

    public ObjectProperty<LocalDate> travelDateProperty() {
        return travelDate;
    }

    public String getClassType() {
        return classType.get();
    }

    public StringProperty classTypeProperty() {
        return classType;
    }

    public int getTotalSeats() {
        return totalSeats.get();
    }

    public IntegerProperty totalSeatsProperty() {
        return totalSeats;
    }

    public int getAvailableSeats() {
        return availableSeats.get();
    }

    public IntegerProperty availableSeatsProperty() {
        return availableSeats;
    }

    // Setters
    public void setTrainId(int trainId) {
        this.trainId.set(trainId);
    }

    public void setTravelDate(LocalDate travelDate) {
        this.travelDate.set(travelDate);
    }

    public void setClassType(String classType) {
        this.classType.set(classType);
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats.set(totalSeats);
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats.set(availableSeats);
    }
}
