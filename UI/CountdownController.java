package UI;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by alex on 10/3/2016.
 */
public class CountdownController extends HBox implements Initializable {
    /* GUI */
    @FXML private TextFlow countdownText;
    @FXML private ProgressIndicator countdownProgress;

    /* Communicate with other controllers */
    private NewInterface application;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
                Text message=updateCountdownText(3);
        countdownText.getChildren().add(message);
    }

    public void setApp(NewInterface app){application=app;}

    /* helper functions */
    private Text updateCountdownText(int second){
        Text message=new Text();
        message.setText("Recording will be ready after "+String.valueOf(second)+" second(s).");
        message.setFill(Color.RED);
        return message;
    }
}
