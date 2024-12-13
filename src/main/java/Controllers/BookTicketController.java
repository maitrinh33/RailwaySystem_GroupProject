package Controllers;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import javafx.fxml.Initializable;

public class BookTicketController implements Initializable {

    @FXML
    private DatePicker departureDatePicker; // DatePicker for departure date

    @FXML
    private ComboBox<String> trainName; // ComboBox for train name

    @FXML
    private ComboBox<String> departureStation; // ComboBox for departure station

    @FXML
    private ComboBox<String> arrivalStation; // ComboBox for arrival station

    @FXML
    private ComboBox<Integer> coach; // ComboBox for coach number

    @FXML
    private ComboBox<String> classType; // ComboBox for class type

    @FXML
    private ComboBox<String> seat; // ComboBox for seat IDs

    // Method to open the Check Ticket scene
    public void openCheckTicket() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CheckTicket.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setTitle("Check Ticket Availability");
        stage.setScene(new Scene(root));
        stage.show();
    }

    // Method to fill ticket details from selected seat and train
    public void fillTicketDetails(String date, String trainName, String departureStation, String arrivalStation,
                                   int coachNumber, String classType, List<String> seatIds) {
        if (departureDatePicker != null) {
            departureDatePicker.setValue(LocalDate.parse(date)); // Set the departure date
        }
        
        if (this.trainName != null) {
            this.trainName.setValue(trainName); // Set selected train name
        }

        if (this.departureStation != null) {
            this.departureStation.setValue(departureStation); // Set selected departure station
        }

        if (this.arrivalStation != null) {
            this.arrivalStation.setValue(arrivalStation); // Set selected arrival station
        }

        if (this.coach != null) {
            this.coach.setValue(coachNumber); // Set selected coach number
        }

        if (this.classType != null) {
            this.classType.setValue(classType); // Set selected class type
        }

        if (this.seat != null) {
            seat.getItems().clear(); // Clear previous seat IDs
            seat.getItems().addAll(seatIds); // Add selected seat IDs
            // Optionally set the first seat ID as selected
            if (!seatIds.isEmpty()) {
                seat.setValue(seatIds.get(0));
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }
}