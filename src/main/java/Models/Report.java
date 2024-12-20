package Models;

import java.time.LocalDate;

public class Report {
    private String trainName;
    private int totalTicketsSold;
    private LocalDate reportDate;
    private double totalRevenue;
    private double totalProfit;

    // Constructor including reportDate
    public Report(String trainName, double totalRevenue, int totalTicketsSold, double totalProfit, LocalDate reportDate) {
        this.trainName = trainName;
        this.totalRevenue = totalRevenue;
        this.totalTicketsSold = totalTicketsSold;
        this.totalProfit = totalProfit;
        this.reportDate = reportDate; // Set the report date
    }

    // Getters
    public String getTrainName() {
        return trainName;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public int getTotalTicketsSold() {
        return totalTicketsSold;
    }

    public double getTotalProfit() {
        return totalProfit;
    }

    public LocalDate getReportDate() {
        return reportDate;
    }

    // Setters
    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public void setTotalTicketsSold(int totalTicketsSold) {
        this.totalTicketsSold = totalTicketsSold;
    }

    public void setTotalProfit(double totalProfit) {
        this.totalProfit = totalProfit;
    }

    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }

    // Optional toString method for debugging
    @Override
    public String toString() {
        return "Report{" +
                "trainName='" + trainName + '\'' +
                ", totalTicketsSold=" + totalTicketsSold +
                ", reportDate=" + reportDate +
                ", totalRevenue=" + totalRevenue +
                ", totalProfit=" + totalProfit +
                '}';
    }
}
