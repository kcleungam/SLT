/**
 * Created by Luke on 27/1/2016.
 */

import com.leapmotion.leap.Controller;
import com.sun.javafx.tk.Toolkit;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.concurrent.*;

import java.net.URL;
import java.time.Duration;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

public class InterfaceFXController implements Initializable{
    private static Database db = new Database("Signs", "HK_Signs");
    private static SignBank allSigns;
    private SampleListener sampleListener = new SampleListener();
    private Controller controller = new Controller();
    private DTW dtw = new DTW();
    private static boolean recording = false;

    @FXML private Label message;
    @FXML private TextField txtfName;
    @FXML private ListView gestureList;
    private ObservableList<String> gestures;

    @Override
    public void initialize(URL location, ResourceBundle resources){
        try {
            // Add listener to grab data
            controller.addListener(sampleListener);
            allSigns = new SignBank(db);

            gestures = FXCollections.observableArrayList();

            for (String key : allSigns.getAllSigns().keySet()) {
                gestures.add(key);
            }

            gestureList.setItems(gestures);
        } catch (Exception e) {
            message.setText("Exception caught when initializing the system!");
            e.printStackTrace();
        }



    }

    @FXML protected void newButtonAction(ActionEvent event) {
        if (!recording) {
            recording = true;

            Sign sign;
            String signName = txtfName.getText();

            if (signName.equals("")) {
                message.setText("Please enter a name!");
                recording = false;
                return;
            }

            if (allSigns.getAllSigns().containsKey(signName)) {
                message.setText("This name is already existed in the database.");
            } else {
                ready();
                /*sampleListener.reset();
                sampleListener.gainFocus();

                try {
                    while (true) {
                        if (sampleListener.checkFinish()) {
                            if (sampleListener.checkValid()) {
                                System.out.println(recording);
                                //if (savePrompt()) {
                                //recordSign(sampleListener.returnOneSample(), signName, sign, allsign);
                                sign = new Sign(signName, new Sample(sampleListener.returnOneSample()));
                                //ToDo: handle the boolean return value aftter adding the given sign
                                allSigns.addSign(signName, sign);
                                db.addSign(sign);
                                message.setText("New gesture: " + signName);
                                gestures.add(signName);
                                //}
                            } else {
                                message.setText("The recording is invalid.");
                            }
                            recording = false;
                            break;
                        }
                        // The current thread is too fast, will fail to
                        // trace Listener if missing this code
                        Thread.currentThread().sleep(10);
                    }
                } catch (Exception e) {
                    message.setText("Exception caught!");
                    e.printStackTrace();
                    recording = false;
                }
                */
                recording = false;
                sampleListener.lostFocus();
            }

        }

    }

        @FXML protected void trainButtonAction (ActionEvent event){
            message.setText("Train button pressed");
        }

        @FXML protected void removeButtonAction (ActionEvent event){
            /*Service<Void> service = new Service<Void>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            //Background work
                            final CountDownLatch latch = new CountDownLatch(1);
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    try{
                                        //FX Stuff done here
                                        Timer timer = new Timer();
                                        timer.scheduleAtFixedRate(new TimerTask() {
                                            int count = 3;

                                            @Override
                                            public void run() {
                                                message.setText("" + count--);
                                            }
                                        }, 0, 1000);
                                    }finally{
                                        latch.countDown();
                                    }
                                }
                            });
                            latch.await();
                            //Keep with the background work
                            return null;
                        }
                    };
                }
            };

            service.start();*/

            message.setText("Remove button pressed");
        }

        @FXML protected void systemButtonAction (ActionEvent event){
            message.setText("System button pressed");
        }

        /**
         * Provide a 3-second countdown.
         */
        public void ready(){
            message.setText("Countdown!");

            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                int count = 3;

                @Override
                public void run() {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            message.setText("" + count--);
                            if(count < 0) timer.cancel();
                        }
                    });
                }
            }, 1000, 1000);
    }
}
