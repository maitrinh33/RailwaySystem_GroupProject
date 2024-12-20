package Controllers;

import Models.Report;
import Utils.DatabaseConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.chart.StackedBarChart;
import javafx.event.ActionEvent;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportsController {

    @FXML
    private StackedBarChart<String, Number> revenueChart;

    @FXML
    private BarChart<String, Number> ticketSoldChart;

    @FXML
    private CategoryAxis revenueXAxis;

    @FXML
    private CategoryAxis ticketSoldXAxis;

    @FXML
    private Label totalTicketsLabel;

    @FXML
    private Label totalRevenueLabel;

    @FXML
    private Label profitLabel;

    @FXML
    private ComboBox<String> timePeriodComboBox; // Added ComboBox for selecting time period


    @FXML
    public void initialize() {
        // Initialize the ComboBox with time periods
        timePeriodComboBox.setItems(FXCollections.observableArrayList("Weekly", "Monthly", "Yearly"));
        timePeriodComboBox.setValue("Weekly");  // Set default to Weekly

        // Load data for the last 7 days as the default
        LocalDate startDate = LocalDate.now().minusWeeks(1); // Last 7 days
        String query = "SELECT report_date, SUM(total_revenue) AS total_revenue, SUM(total_profit) AS total_profit, " +
                       "SUM(total_bookings) AS total_bookings " +
                       "FROM report WHERE report_date >= CURDATE() - INTERVAL 7 DAY GROUP BY report_date";

        List<Report> reportDataList = fetchReportData(query);
        updateBarChart(reportDataList, "Weekly"); // Pass "Weekly" as the selected period
        updateLabels(reportDataList);
    }

    private List<Report> fetchReportData(String query) {
        List<Report> data = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection(); // Use DatabaseConnection to get the connection
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Report report = new Report(
                        "N/A", // Assuming you don't have a train_name, use a placeholder or modify your schema
                        rs.getDouble("total_revenue"),
                        rs.getInt("total_bookings"), // Assuming 'total_bookings' maps to 'total_tickets_sold'
                        rs.getDouble("total_profit"),
                        rs.getDate("report_date").toLocalDate()  // Convert SQL date to LocalDate
                );
                data.add(report);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log exception for debugging
        }
        return data;
    }

@FXML
private void onSearch(ActionEvent event) {
    String selectedPeriod = timePeriodComboBox.getValue();  // Get selected period
    String query = "";
    LocalDate currentDate = LocalDate.now();
    LocalDate startDate;

    // Determine the start date based on selected period
    if ("Weekly".equals(selectedPeriod)) {
        startDate = currentDate.minusWeeks(1);  // Last 7 days
        query = "SELECT report_date, SUM(total_revenue) AS total_revenue, SUM(total_profit) AS total_profit, " +
                "SUM(total_bookings) AS total_bookings FROM report " +
                "WHERE report_date >= '" + startDate + "' GROUP BY report_date";
    } else if ("Monthly".equals(selectedPeriod)) {
        startDate = currentDate.minusDays(30);  // Last 30 days
        query = "SELECT report_date, SUM(total_revenue) AS total_revenue, SUM(total_profit) AS total_profit, " +
                "SUM(total_bookings) AS total_bookings FROM report " +
                "WHERE report_date >= '" + startDate + "' GROUP BY report_date";
    } else { // Yearly
        query = "SELECT DATE_FORMAT(report_date, '%Y-%m') AS month, SUM(total_revenue) AS total_revenue, SUM(total_profit) AS total_profit, " +
                "SUM(total_bookings) AS total_bookings FROM report " +
                "WHERE report_date >= DATE_SUB(CURDATE(), INTERVAL 1 YEAR) GROUP BY month";
    }

    // Fetch the data from the database
    List<Report> reportDataList = fetchReportData(query);

    // Update the bar chart with the fetched data
    updateBarChart(reportDataList, selectedPeriod); // Pass the selectedPeriod
    updateLabels(reportDataList);  // Update the labels as well
}


private void updateBarChart(List<Report> reportDataList, String selectedPeriod) {
    // Clear existing data
    revenueChart.getData().clear();
    ticketSoldChart.getData().clear();

    // Prepare series for revenue and profit
    XYChart.Series<String, Number> revenueSeries = new XYChart.Series<>();
    XYChart.Series<String, Number> profitSeries = new XYChart.Series<>();

    Map<String, Double> revenueMap = new HashMap<>();
    Map<String, Double> profitMap = new HashMap<>();

    if ("Weekly".equals(selectedPeriod)) {
        // Weekly: Last 7 days
        LocalDate today = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String dateLabel = date.toString(); // Use ISO date format
            revenueMap.put(dateLabel, 0.0);
            profitMap.put(dateLabel, 0.0);
        }
    } else if ("Monthly".equals(selectedPeriod)) {
        // Monthly: Last 30 days
        LocalDate today = LocalDate.now();
        for (int i = 29; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String dateLabel = date.toString();
            revenueMap.put(dateLabel, 0.0);
            profitMap.put(dateLabel, 0.0);
        }
    } else if ("Yearly".equals(selectedPeriod)) {
        // Yearly: Last 12 months
        LocalDate today = LocalDate.now();
        for (int i = 11; i >= 0; i--) {
            LocalDate date = today.minusMonths(i);
            String monthLabel = date.getMonth().name(); // Get month name
            revenueMap.put(monthLabel, 0.0);
            profitMap.put(monthLabel, 0.0);
        }
    }

    // Fill data from reportDataList
    for (Report report : reportDataList) {
        String label = ("Yearly".equals(selectedPeriod))
                ? report.getReportDate().getMonth().name() // Month name for yearly
                : report.getReportDate().toString(); // Date string for weekly/monthly
        revenueMap.put(label, report.getTotalRevenue());
        profitMap.put(label, report.getTotalProfit());
    }

    // Populate the chart series
    for (String label : revenueMap.keySet()) {
        revenueSeries.getData().add(new XYChart.Data<>(label, revenueMap.get(label)));
        profitSeries.getData().add(new XYChart.Data<>(label, profitMap.get(label)));
    }

    // Set series names
    revenueSeries.setName("Revenue");
    profitSeries.setName("Profit");

    // Add the series to the chart
    revenueChart.getData().addAll(revenueSeries, profitSeries);
}

    
    private void updateLabels(List<Report> reportDataList) {
        double totalRevenue = reportDataList.stream()
                                            .mapToDouble(Report::getTotalRevenue)
                                            .sum();

        double totalProfit = reportDataList.stream()
                                           .mapToDouble(Report::getTotalProfit)
                                           .sum();

        int totalTickets = reportDataList.size(); // Assuming each report entry represents one ticket

        totalRevenueLabel.setText(String.format("$%.2f", totalRevenue));
        profitLabel.setText(String.format("$%.2f", totalProfit));
        totalTicketsLabel.setText(String.format("%d", totalTickets));
    }

}
