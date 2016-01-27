/**
 * Created by Luke on 27/1/2016.
 */

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class InterfaceFXController {
    @FXML private Label message;

    @FXML protected void newButtonAction(ActionEvent event) {
        message.setText("New button pressed");
    }

    @FXML protected void trainButtonAction(ActionEvent event) {
        message.setText("Train button pressed");
    }
}
