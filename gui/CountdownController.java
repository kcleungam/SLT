package gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.net.URL;
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
                Platform.runLater(() -> {
                    //update the GUI elements
                    countdownText.getChildren().clear();
                    countdownText.getChildren().add(updateCountdownText((int)countdownTime));
                    countdownProgress.setProgress((3.0-countdownTime--)/3.0);
                    //handle the complement of countdown
                    if(countdownProgress.getProgress()>1.0){
                        countdownTimer.cancel();
                        defaultController.closeCountdown();
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
