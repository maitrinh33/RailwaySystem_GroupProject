package Models;

import javafx.beans.property.*;

import java.sql.Date;

public class Booking {
    private IntegerProperty bookingId;
    private StringProperty ticketId;
    private IntegerProperty paymentId;
    private StringProperty customerName;
    private StringProperty passport;
    private DoubleProperty paymentAmount;
    private StringProperty bookingTime;
    private StringProperty departureDate;
    private StringProperty trainName;
    private StringProperty route;
    private StringProperty classType;
    private StringProperty seatNumber;
    private StringProperty coachNumber;
    private StringProperty status;

    // Constructor
    public Booking(int bookingId, String ticketId, String customerName, String passport,
                   double paymentAmount, String bookingTime, String departureDate, String trainName,
                   String route, String classType, String seatNumber, String coachNumber, String status) {
        this.bookingId = new SimpleIntegerProperty(bookingId);
        this.ticketId = new SimpleStringProperty(ticketId);
        this.customerName = new SimpleStringProperty(customerName);
        this.passport = new SimpleStringProperty(passport);
        this.paymentAmount = new SimpleDoubleProperty(paymentAmount);
        this.bookingTime = new SimpleStringProperty(bookingTime);
        this.departureDate = new SimpleStringProperty(departureDate);
        this.trainName = new SimpleStringProperty(trainName);
        this.route = new SimpleStringProperty(route);
        this.classType = new SimpleStringProperty(classType);
        this.seatNumber = new SimpleStringProperty(seatNumber);
        this.coachNumber = new SimpleStringProperty(coachNumber);
        this.status = new SimpleStringProperty(status);
    }

    // Getters and Setters
    public int getBookingId() {
        return bookingId.get();
    }

    public void setBookingId(int bookingId) {
        this.bookingId.set(bookingId);
    }

    public IntegerProperty bookingIdProperty() {
        return bookingId;
    }

    public String getTicketId() {
        return ticketId.get();
    }

    public void setTicketId(String ticketId) {
        this.ticketId.set(ticketId);
    }

    public StringProperty ticketIdProperty() {
        return ticketId;
    }

    public IntegerProperty paymentIdProperty() {
        return paymentId;
    }

    public String getCustomerName() {
        return customerName.get();
    }

    public void setCustomerName(String customerName) {
        this.customerName.set(customerName);
    }

    public StringProperty customerNameProperty() {
        return customerName;
    }

    public String getPassport() {
        return passport.get();
    }

    public void setPassport(String passport) {
        this.passport.set(passport);
    }

    public StringProperty passportProperty() {
        return passport;
    }

    public double getPaymentAmount() {
        return paymentAmount.get();
    }

    public void setPaymentAmount(double paymentAmount) {
        this.paymentAmount.set(paymentAmount);
    }

    public DoubleProperty paymentAmountProperty() {
        return paymentAmount;
    }

    public String getBookingTime() {
        return bookingTime.get();
    }

    public void setBookingTime(String bookingTime) {
        this.bookingTime.set(bookingTime);
    }

    public StringProperty bookingTimeProperty() {
        return bookingTime;
    }

    public String getDepartureDate() {
        return departureDate.get();
    }

    public void setDepartureDate(String departureDate) {
        this.departureDate.set(departureDate);
    }

    public StringProperty departureDateProperty() {
        return departureDate;
    }

    public String getTrainName() {
        return trainName.get();
    }

    public void setTrainName(String trainName) {
        this.trainName.set(trainName);
    }

    public StringProperty trainNameProperty() {
        return trainName;
    }

    public String getRoute() {
        return route.get();
    }

    public void setRoute(String route) {
        this.route.set(route);
    }

    public StringProperty routeProperty() {
        return route;
    }

    public String getClassType() {
        return classType.get();
    }

    public void setClassType(String classType) {
        this.classType.set(classType);
    }

    public StringProperty classTypeProperty() {
        return classType;
    }

    public String getSeatNumber() {
        return seatNumber.get();
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber.set(seatNumber);
    }

    public StringProperty seatNumberProperty() {
        return seatNumber;
    }

    public String getCoachNumber() {
        return coachNumber.get();
    }

    public void setCoachNumber(String coachNumber) {
        this.coachNumber.set(coachNumber);
    }

    public StringProperty coachNumberProperty() {
        return coachNumber;
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public StringProperty statusProperty() {
        return status;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId=" + bookingId.get() +
                ", ticketId='" + ticketId.get() + '\'' +
                ", customerName='" + customerName.get() + '\'' +
                ", passport='" + passport.get() + '\'' +
                ", paymentAmount=" + paymentAmount.get() +
                ", bookingTime='" + bookingTime.get() + '\'' +
                ", departureDate='" + departureDate.get() + '\'' +
                ", trainName='" + trainName.get() + '\'' +
                ", route='" + route.get() + '\'' +
                ", classType='" + classType.get() + '\'' +
                ", seatNumber='" + seatNumber.get() + '\'' +
                ", coachNumber='" + coachNumber.get() + '\'' +
                ", status='" + status.get() + '\'' +
                '}';
    }
}
