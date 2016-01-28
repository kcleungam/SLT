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
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.concurrent.*;
import java.net.URL;
import java.util.HashMap;
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
    private Service<Void> dtwThread;

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

        // Service responsible for DTW
        dtwThread = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        Boolean validRec = false;

                        while (true) {
                            if (!recording) {
                                sampleListener.reset();
                                sampleListener.gainFocus();

                                Sample rSample = new Sample();

                                try {
                                    while (true) {
                                        if (!recording) {
                                            if (sampleListener.checkFinish()) {
                                                if (sampleListener.checkValid()) {

                                                    rSample = new Sample(sampleListener.returnOneSample());
                                                    validRec = true;

                                                } else {
                                                    System.out.println("The recording is invalid. ");
                                                    validRec = false;
                                                    Thread.currentThread().sleep(10);
                                                }
                                                break;
                                            }
                                            // The current thread is too fast, will fail to
                                            // trace Listener if missing this code
                                            Thread.currentThread().sleep(10);
                                        }else{
                                            Thread.currentThread().sleep(10);       // !!!!!!Remember to add else and add sleep, I spend a lot of time to find this bug!!!!!
                                        }
                                    }

                                    sampleListener.lostFocus();
                                    if (!recording) {
                                        if (validRec == true) {
                                            dtw.setRSample(rSample);//TODO: if rSample is created by default constructor, error may occurs

                                            // Retrieve Sign with finger count and hand type
                                            HashMap<String, Sign> signByBoth = db.getSignsByBoth(rSample.allFingers.count, rSample.allHands.type);

                                            for (Sign storedSign : signByBoth.values()) {
                                                dtw.setStoredSign(storedSign);
                                                dtw.calDTW();
                                            }

                                            dtw.printResult();
                                            if (dtw.result.equals("Unknown Gesture")) {
                                                System.out.println("Unknown Gesture!");
                                            } else {
                                                updateMessage("The most similar gesture is " + dtw.result);
                                                System.out.println("The most similar gesture is " + dtw.result);
                                                System.out.println("The minimum cost of DTW is " + dtw.bestMatch);
                                            }

                                            dtw.reset();
                                            Thread.currentThread().sleep(20);
                                        } else {
                                            dtw.reset();
                                            Thread.currentThread().sleep(20);
                                        }        //   do nothing
                                    }
                                }catch(Exception e){
                                    e.printStackTrace();
                                }

                            }else{
                                try {
                                    Thread.currentThread().sleep(10);
                                } catch (Exception ex) {
                                }
                            }
                        }
                    }
                };
            }
        };

        message.textProperty().bind(dtwThread.messageProperty());
        dtwThread.start();
    }

    @FXML protected void newButtonAction(ActionEvent event) {
        Service<Void> newThread = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        message.textProperty().unbind();

                        if (!recording) {
                            recording = true;

                            Sign sign;
                            String signName = txtfName.getText();

                            if (signName.equals("")) {
                                updateMessage("Please enter a name!");
                                recording = false;
                                return null;
                            }

                            if (allSigns.getAllSigns().containsKey(signName)) {
                                updateMessage("This name is already existed in the database.");
                            } else {
                                //ready();
                                sampleListener.reset();
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
                                                updateMessage("New gesture " + signName + " is created!");
                                                gestures.add(signName);
                                                //}
                                            } else {
                                                updateMessage("The recording is invalid.");
                                            }
                                            recording = false;
                                            break;
                                        }
                                        // The current thread is too fast, will fail to
                                        // trace Listener if missing this code
                                        Thread.currentThread().sleep(10);
                                    }
                                } catch (Exception e) {
                                    updateMessage("Exception caught!");
                                    e.printStackTrace();
                                    recording = false;
                                }

                                recording = false;
                                sampleListener.lostFocus();
                            }

                        }
                        return null;
                    }
                };
            }
        };

        newThread.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                message.textProperty().unbind();
                message.textProperty().bind(dtwThread.messageProperty());
            }
        });

        message.textProperty().bind(newThread.messageProperty());
        newThread.start();
    }

        @FXML protected void trainButtonAction (ActionEvent event) {
            Service<Void> trainThread = new Service<Void>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            message.textProperty().unbind();

                            if (!recording) {
                                recording = true;

                                Runnable TrainRunnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        Sign sign;

                                        //printTraining();
                                        //System.out.println("Please enter the name of the sign you want to train: ");
                                        String trainName = (String) gestureList.getSelectionModel().getSelectedItem();

                                        if (allSigns.getAllSigns().containsKey(trainName)) {
                                            updateMessage("Sign found, ready to start training");

                                            // recordingMode = true;
                                            ready();

                                            // SampleListener trainListener = new
                                            // SampleListener(); //new a listener everytime
                                            // controller.addListener(trainListener); // add
                                            // listener, grab data
                                            sampleListener.reset();
                                            sampleListener.gainFocus();
                                            /**
                                             * keep grabbing the frame from controller, check
                                             * whether it is recordable, add to the oneSample if
                                             * it is. """"put it as one sample of specific
                                             * sign"""
                                             */
                                            try {
                                                while (true) {
                                                    if (sampleListener.checkFinish()) {
                                                        if (sampleListener.checkValid()) {
                                                            //if (savePrompt()) {
                                                            //trainOneSign(sampleListener.returnOneSample(), trainName, allsign);
                                                            //ToDo: handle the boolean return type after adding sample to the given sign
                                                            sign = new Sign(trainName, new Sample(sampleListener.returnOneSample()));
                                                            allSigns.addSign(trainName, sign);
                                                            db.addSign(sign);
                                                            updateMessage("Training completed!");
                                                            //}
                                                        } else {
                                                            updateMessage("The recording is invalid!");
                                                        }
                                                        recording = false;
                                                        break;
                                                    }
                                                    // It is necessary, without it, bug occur
                                                    Thread.currentThread().sleep(10);
                                                }
                                            } catch (Exception e) {
                                                updateMessage("Exception caught!");
                                                e.printStackTrace();

                                                recording = false;
                                            }

                                            // controller.removeListener(trainListener);
                                            sampleListener.lostFocus();
							/*
							 * while (true) { Frame frame = controller.frame();
							 * if (recordingMode == true) {
							 * trainOneSign(frame,oneSample,trainName,allsign);
							 * //recordingMode = false at the end of
							 * trainOneSign } else { break; } }
							 */
                                        } else { // sign not found
                                            updateMessage("Please choose a sign!");
                                            recording = false;
                                        }
                                        recording = false;
                                        return;
                                    }
                                };
                            }

                            return null;
                        }
                    };
                }
            };

            trainThread.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    message.textProperty().unbind();
                    message.textProperty().bind(dtwThread.messageProperty());
                }
            });

            message.textProperty().bind(trainThread.messageProperty());
            trainThread.start();
        }

        @FXML protected void removeButtonAction (ActionEvent event){
            String signName = (String) gestureList.getSelectionModel().getSelectedItem();

            if(signName == null){
                message.setText("Please select a sign.");
                return;
            }

            try{
                Sign sign = db.getSignsByName(signName);
                db.removeSign(sign);
                allSigns.removeSign(signName);
                gestures.remove(signName);
                message.setText("Gesture " + signName + " is removed!");
            }catch(Exception e){
                message.setText("Exception caught!");
                e.printStackTrace();
            }
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
