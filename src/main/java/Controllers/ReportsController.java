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
    private ComboBox<String> timePeriodComboBox; 


    @FXML
    public void initialize() {
        timePeriodComboBox.setItems(FXCollections.observableArrayList("Weekly", "Monthly", "Yearly"));
        timePeriodComboBox.setValue("Weekly");  

        LocalDate startDate = LocalDate.now().minusWeeks(1); 
        String query = "SELECT report_date, SUM(total_revenue) AS total_revenue, SUM(total_profit) AS total_profit, " +
                       "SUM(total_bookings) AS total_bookings " +
                       "FROM report WHERE report_date >= CURDATE() - INTERVAL 7 DAY GROUP BY report_date";

        List<Report> reportDataList = fetchReportData(query);
        updateBarChart(reportDataList, "Weekly"); 
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
            e.printStackTrace();
        }
        return data;
    }

@FXML
private void onSearch(ActionEvent event) {
    String selectedPeriod = timePeriodComboBox.getValue(); 
    String query = "";
    LocalDate currentDate = LocalDate.now();
    LocalDate startDate;

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

    List<Report> reportDataList = fetchReportData(query);

    updateBarChart(reportDataList, selectedPeriod); 
    updateLabels(reportDataList);  
}

    private void updateBarChart(List<Report> reportDataList, String selectedPeriod) {
        revenueChart.getData().clear();
        ticketSoldChart.getData().clear();

        XYChart.Series<String, Number> revenueSeries = new XYChart.Series<>();
        XYChart.Series<String, Number> profitSeries = new XYChart.Series<>();

        Map<String, Double> revenueMap = new HashMap<>();
        Map<String, Double> profitMap = new HashMap<>();

        if ("Weekly".equals(selectedPeriod)) {
            LocalDate today = LocalDate.now();
            for (int i = 6; i >= 0; i--) {
                LocalDate date = today.minusDays(i);
                String dateLabel = date.toString(); 
                revenueMap.put(dateLabel, 0.0);
                profitMap.put(dateLabel, 0.0);
            }
        } else if ("Monthly".equals(selectedPeriod)) {
            LocalDate today = LocalDate.now();
            for (int i = 29; i >= 0; i--) {
                LocalDate date = today.minusDays(i);
                String dateLabel = date.toString();
                revenueMap.put(dateLabel, 0.0);
                profitMap.put(dateLabel, 0.0);
            }
        } else if ("Yearly".equals(selectedPeriod)) {
            LocalDate today = LocalDate.now();
            for (int i = 11; i >= 0; i--) {
                LocalDate date = today.minusMonths(i);
                String monthLabel = date.getMonth().name();
                revenueMap.put(monthLabel, 0.0);
                profitMap.put(monthLabel, 0.0);
            }
        }

        for (Report report : reportDataList) {
            String label = ("Yearly".equals(selectedPeriod))
                    ? report.getReportDate().getMonth().name() 
                    : report.getReportDate().toString(); 
            revenueMap.put(label, report.getTotalRevenue());
            profitMap.put(label, report.getTotalProfit());
        }

        for (String label : revenueMap.keySet()) {
            revenueSeries.getData().add(new XYChart.Data<>(label, revenueMap.get(label)));
            profitSeries.getData().add(new XYChart.Data<>(label, profitMap.get(label)));
        }

        revenueSeries.setName("Revenue");
        profitSeries.setName("Profit");

        revenueChart.getData().addAll(revenueSeries, profitSeries);
    }

    
    private void updateLabels(List<Report> reportDataList) {
        double totalRevenue = reportDataList.stream()
                                            .mapToDouble(Report::getTotalRevenue)
                                            .sum();

        double totalProfit = reportDataList.stream()
                                           .mapToDouble(Report::getTotalProfit)
                                           .sum();

        int totalTickets = reportDataList.size(); 

        totalRevenueLabel.setText(String.format("$%.2f", totalRevenue));
        profitLabel.setText(String.format("$%.2f", totalProfit));
        totalTicketsLabel.setText(String.format("%d", totalTickets));
    }
}
