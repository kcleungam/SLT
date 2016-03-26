package UI;

import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
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
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by alex on 10/3/2016.
 */
public class CountdownController extends HBox implements Initializable {
    /* GUI */
    @FXML private TextFlow countdownText;
    @FXML private ProgressIndicator countdownProgress;
    private double countdownTime;

    /* Communicate with other controllers */
    private DefaultController defaultController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //TODO: can we do this with exactly 3 seconds?
        //Remarks: actually it needs 4 seconds to get back
        //Just for showing progress indicator done. Otherwise, the pop-up window will be closed before the progress indicator updated to 100%
        countdownTime=3.0;
        Timer countdownTimer=new Timer();
        countdownTimer.schedule(new TimerTask(){//update every second
            @Override
            public void run() {
                //set progress
                Platform.runLater(new Runnable() {//JavaFX's style of controlling threads with non-JavaFX threads.
                    @Override
                    public void run() {
                        //update the GUI elements
                        countdownText.getChildren().clear();
                        countdownText.getChildren().add(updateCountdownText((int)countdownTime));
                        countdownProgress.setProgress((3.0-countdownTime--)/3.0);
                        //handle the complement of countdown
                        if(countdownProgress.getProgress()>1.0){
                            countdownTimer.cancel();
                            defaultController.closeCountdown();
                        }
                    }
                });
            }
        },0,1000);
    }

    public void setApp(DefaultController defaultController){this.defaultController=defaultController;}

    /* helper functions */
    public static Text updateCountdownText(int second){
        Text message=new Text();
        message.setText("Recording will be ready after "+String.valueOf(second)+" second(s).");
        message.setFill(Color.RED);
        return message;
    }
}
