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
import javafx.fxml.FXMLLoader;

/**
 * FXML Controller class
 *
 * @author dopha
 */
public class BookTicketController implements Initializable {

    public void openCheckTicket() throws IOException {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CheckTicket.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Check Ticket Availability");
            stage.setScene(new Scene(root));
            stage.show();
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

