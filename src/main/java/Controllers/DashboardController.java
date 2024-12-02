package Controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.fxml.FXML;

public class DashboardController implements Initializable {

    @FXML
    public BarChart<String, Number> barChart;

    @FXML
    public LineChart<String, Number> lineChart;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Sample data for BarChart
        XYChart.Series<String, Number> barChartSeries = new XYChart.Series<>();
        barChartSeries.setName("Revenue");
        barChartSeries.getData().add(new XYChart.Data<>("January", 1000));
        barChartSeries.getData().add(new XYChart.Data<>("February", 1500));
        barChartSeries.getData().add(new XYChart.Data<>("March", 2000));
        
        barChart.getData().add(barChartSeries);

        // Sample data for LineChart
        XYChart.Series<String, Number> lineChartSeries = new XYChart.Series<>();
        lineChartSeries.setName("Ticket Sales");
        lineChartSeries.getData().add(new XYChart.Data<>("January", 500));
        lineChartSeries.getData().add(new XYChart.Data<>("February", 600));
        lineChartSeries.getData().add(new XYChart.Data<>("March", 700));

        lineChart.getData().add(lineChartSeries);
    }
}
