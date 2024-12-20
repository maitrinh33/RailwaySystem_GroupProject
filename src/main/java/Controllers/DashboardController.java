package Controllers;

import Models.Report;
import Utils.DatabaseConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

public class DashboardController {

    @FXML
    private StackedBarChart<String, Number> revenueChart;

    @FXML
    private LineChart<String, Number> lineChart;

    @FXML
    private Label totalTicketsLabel;

    @FXML
    private Label totalRevenueLabel;

    @FXML
    private Label profitLabel;

    @FXML
    private Label paymentLabel;

    @FXML
    public void initialize() {
        loadWeeklyReportData();
    }

    private void loadWeeklyReportData() {
        String query = """
            SELECT report_date, SUM(total_revenue) AS total_revenue, 
                   SUM(total_profit) AS total_profit, SUM(total_bookings) AS total_bookings
            FROM report 
            WHERE report_date BETWEEN DATE_SUB(CURDATE(), INTERVAL 7 DAY) AND CURDATE()
            GROUP BY report_date
            """;

        List<Report> reportDataList = fetchReportData(query);
        updateBarChart(reportDataList);
        updateLineChart(reportDataList);
        updateLabels(reportDataList);
    }

    private List<Report> fetchReportData(String query) {
        List<Report> data = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Report report = new Report(
                    "N/A",
                    rs.getDouble("total_revenue"),
                    rs.getInt("total_bookings"),
                    rs.getDouble("total_profit"),
                    rs.getDate("report_date").toLocalDate()
                );
                data.add(report);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching report data: " + e.getMessage());
        }
        return data;
    }

    private void updateBarChart(List<Report> reportDataList) {
        revenueChart.getData().clear();

        XYChart.Series<String, Number> revenueSeries = new XYChart.Series<>();
        XYChart.Series<String, Number> profitSeries = new XYChart.Series<>();

        LocalDate today = LocalDate.now();
        Map<String, Double> revenueMap = new HashMap<>();
        Map<String, Double> profitMap = new HashMap<>();

        // Pre-fill the last 7 days with zero values
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String dateLabel = date.toString();
            revenueMap.put(dateLabel, 0.0);
            profitMap.put(dateLabel, 0.0);
        }

        // Populate the maps with actual data
        for (Report report : reportDataList) {
            String label = report.getReportDate().toString();
            revenueMap.put(label, report.getTotalRevenue());
            profitMap.put(label, report.getTotalProfit());
        }

        // Add data to series
        for (String dateLabel : revenueMap.keySet()) {
            revenueSeries.getData().add(new XYChart.Data<>(dateLabel, revenueMap.get(dateLabel)));
            profitSeries.getData().add(new XYChart.Data<>(dateLabel, profitMap.get(dateLabel)));
        }

        revenueSeries.setName("Revenue");
        profitSeries.setName("Profit");

        revenueChart.getData().addAll(revenueSeries, profitSeries);
    }

    private void updateLineChart(List<Report> reportDataList) {
        lineChart.getData().clear();

        XYChart.Series<String, Number> bookingSeries = new XYChart.Series<>();

        LocalDate today = LocalDate.now();
        Map<String, Integer> bookingMap = new HashMap<>();

        // Pre-fill the last 7 days with zero values
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String dateLabel = date.toString();
            bookingMap.put(dateLabel, 0);
        }

        // Populate the map with actual data
        for (Report report : reportDataList) {
            String label = report.getReportDate().toString();
            bookingMap.put(label, report.getTotalTicketsSold());
        }

        // Add data to the series
        for (String dateLabel : bookingMap.keySet()) {
            bookingSeries.getData().add(new XYChart.Data<>(dateLabel, bookingMap.get(dateLabel)));
        }

        bookingSeries.setName("Total Bookings");

        lineChart.getData().add(bookingSeries);
    }

    private void updateLabels(List<Report> reportDataList) {
        double totalRevenue = reportDataList.stream()
                                            .mapToDouble(Report::getTotalRevenue)
                                            .sum();
        double totalProfit = reportDataList.stream()
                                           .mapToDouble(Report::getTotalProfit)
                                           .sum();
        int totalTickets = reportDataList.stream()
                                         .mapToInt(Report::getTotalTicketsSold)
                                         .sum();

        totalRevenueLabel.setText(String.format("$%.1f", totalRevenue));
        profitLabel.setText(String.format("$%.1f", totalProfit));
        totalTicketsLabel.setText(String.valueOf(totalTickets));
    }
}
