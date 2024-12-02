/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

 * @author dopha
 */
package Controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;

/**
 * FXML Controller class
 *
 * @author dopha
 */
public class ManageScheduleController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(ManageScheduleController.class.getName());

    public void openSeatInventory() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SeatInventory.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Manage Seat Inventory");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load the FXML", e);
        }
    }

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
}

