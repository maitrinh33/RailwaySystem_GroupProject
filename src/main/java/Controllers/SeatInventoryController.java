package Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import Models.SeatInventory;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.scene.control.ComboBox;

public class SeatInventoryController {

    @FXML
    private TableView<SeatInventory> seatInventoryTable;

    @FXML
    private TableColumn<SeatInventory, Integer> colTrainId;

    @FXML
    private TableColumn<SeatInventory, LocalDate> colTravelDate;

    @FXML
    private TableColumn<SeatInventory, String> colClassType;

    @FXML
    private TableColumn<SeatInventory, Integer> colTotalSeats;

    @FXML
    private TableColumn<SeatInventory, Integer> colAvailableSeats;

    @FXML
    private TextField txtTrainId;

    @FXML
    private DatePicker dateTravelDate;

    @FXML
    private ComboBox<String> comboClassType;

    @FXML
    private TextField txtTotalSeats;

    @FXML
    private TextField txtAvailableSeats;

    private final ObservableList<SeatInventory> inventoryList = FXCollections.observableArrayList();
    
    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize the columns with appropriate properties
        colTrainId.setCellValueFactory(cellData -> cellData.getValue().trainIdProperty().asObject());
        colTravelDate.setCellValueFactory(cellData -> cellData.getValue().travelDateProperty());
        colClassType.setCellValueFactory(cellData -> cellData.getValue().classTypeProperty());
        colTotalSeats.setCellValueFactory(cellData -> cellData.getValue().totalSeatsProperty().asObject());
        colAvailableSeats.setCellValueFactory(cellData -> cellData.getValue().availableSeatsProperty().asObject());

        // Set the items in the combo box
        comboClassType.setItems(FXCollections.observableArrayList("Sleeper", "AC", "General"));

        // Set the table items
        seatInventoryTable.setItems(inventoryList);
    }

    @FXML
    private void handleAdd() {
        try {
            int trainId = Integer.parseInt(txtTrainId.getText());
            LocalDate travelDate = dateTravelDate.getValue();
            String classType = comboClassType.getValue();
            int totalSeats = Integer.parseInt(txtTotalSeats.getText());
            int availableSeats = Integer.parseInt(txtAvailableSeats.getText());

            inventoryList.add(new SeatInventory(trainId, travelDate, classType, totalSeats, availableSeats));
            clearFields();
        } catch (Exception e) {
            showError("Please enter valid data.");
        }
    }

    @FXML
    private void handleUpdate() {
        SeatInventory selected = seatInventoryTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setTrainId(Integer.parseInt(txtTrainId.getText()));
            selected.setTravelDate(dateTravelDate.getValue());
            selected.setClassType(comboClassType.getValue());
            selected.setTotalSeats(Integer.parseInt(txtTotalSeats.getText()));
            selected.setAvailableSeats(Integer.parseInt(txtAvailableSeats.getText()));
            seatInventoryTable.refresh();
            clearFields();
        } else {
            showError("No item selected to update.");
        }
    }

    @FXML
    private void handleDelete() {
        SeatInventory selected = seatInventoryTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            inventoryList.remove(selected);
        } else {
            showError("No item selected to delete.");
        }
    }

    private void clearFields() {
        txtTrainId.clear();
        dateTravelDate.setValue(null);
        comboClassType.setValue(null);
        txtTotalSeats.clear();
        txtAvailableSeats.clear();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.show();
    }
}
