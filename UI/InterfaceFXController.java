package UI; /**
 * Created by Luke on 27/1/2016.
 */

import com.leapmotion.leap.Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import main.*;

public class InterfaceFXController implements Initializable {
    private static Database db = new Database("Signs", "HK_Signs");
    private static SignBank allSigns;
    private SampleListener sampleListener = new SampleListener();
    private Controller controller = new Controller();
    private DTW dtw = new DTW();
    private static boolean recording = false;
    private static boolean recognition = false;
    private static volatile boolean yes = false, no = false;

    @FXML
    private Pane mainPane;
    @FXML
    private Pane dialogPane;
    @FXML
    private VBox systemBox;
    @FXML
    private Label message;
    @FXML
    private TextField txtfName;
    @FXML
    private Button btnYes;
    @FXML
    private Button btnNo;
    @FXML
    private ListView gestureList;
    private ObservableList<String> gestures;
    private Service<Void> dtwThread;

    // the visualizer applet
    private Visualizer visualizer = new Visualizer();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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

        /*visualizer.setBounds(160, 10, 500, 500);
        mainPane.get
        Runnable liveHand = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        visualizer.traceLM(controller.frame());
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Thread livevisual = new Thread(liveHand);
        livevisual.start();*/

        // Service responsible for DTW
        dtwThread = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        Boolean validRec = false;

                        updateMessage("Switch to recognition mode.");

                        while (true) {
                            if (!recording) {
                                sampleListener.reset();
                                sampleListener.gainFocus();

                                Sample rSample=null;

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
                                        } else {
                                            Thread.currentThread().sleep(10);       // !!!!!!Remember to add else and add sleep, I spend a lot of time to find this bug!!!!!
                                        }
                                    }

                                    sampleListener.lostFocus();
                                    if (!recording) {
                                        if (validRec == true) {
                                            dtw.setRSample(rSample);//TODO: if rSample is created by default constructor, error may occurs

                                            // Retrieve Sign with finger count and hand type with fingerCount tolerance
                                            final int tolerance =1;
                                            /* @Depreciated
                                            HashMap<String, Sign> signByBoth = db.getSignsByBoth(rSample.initialFingerCount, rSample.initialHandType);
                                            signByBoth.putAll(db.getSignsByBoth(rSample.initialFingerCount - 1, rSample.initialHandType));
                                            signByBoth.putAll(db.getSignsByBoth(rSample.initialFingerCount + 1, rSample.initialHandType));*/
                                            HashMap<String, Sign> signByBoth = db.getSignsByBoth(rSample.getInitialFingerCount(), rSample.getInitialHandType(),tolerance);

                                            for (Sign storedSign : signByBoth.values()) {
                                                dtw.setStoredSign(storedSign);
                                                dtw.calDTW();
                                            }

                                            dtw.printResult();
                                            if (dtw.result.equals("Unknown Gesture")) {
                                                System.out.println("Unknown Gesture!");
                                            } else {
                                                updateMessage("The most similar gesture is " + dtw.result + ".");
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
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } else {
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

        dtwThread.setOnCancelled(new EventHandler<WorkerStateEvent>() {
                         @Override
                public void handle(WorkerStateEvent event) {
                    message.textProperty().unbind();
                }
            }
        );

        //message.textProperty().bind(dtwThread.messageProperty());
        //dtwThread.start();
    }

    @FXML
    protected void createButtonAction(ActionEvent event) {
        if (!recording) {
            message.textProperty().unbind();
            recording = true;
            String signName = txtfName.getText();

            if (signName.equals("")) {
                message.setText("Please enter a name.");
                //message.textProperty().bind(dtwThread.messageProperty());
                recording = false;

                return;
            } else if (allSigns.getAllSigns().containsKey(signName)) {
                message.setText("This name is already existed in the database.");
                //message.textProperty().bind(dtwThread.messageProperty());
                recording = false;

                return;
            } else {
                Service<Void> newThread = new Service<Void>() {
                    @Override
                    protected Task<Void> createTask() {
                        return new Task<Void>() {
                            @Override
                            protected Void call() throws Exception {
                                Sign sign;

                                for (int count = 3; count > 0; count--) {
                                    try {
                                        if (count != 1)
                                            updateMessage("The recording of gesture " + signName + " will start in " + count + " seconds.");
                                        else
                                            updateMessage("The recording of gesture " + signName + " will start in " + count + " second.");
                                        Thread.sleep(1000);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                updateMessage("The recording of gesture " + signName + " started.");
                                sampleListener.reset();
                                sampleListener.gainFocus();

                                try {
                                    while (true) {
                                        if (sampleListener.checkFinish()) {
                                            if (sampleListener.checkValid()) {
                                                // Reset the variables
                                                yes = false;
                                                no = false;

                                                // Set the buttons visible
                                                btnYes.setVisible(true);
                                                btnNo.setVisible(true);

                                                updateMessage("Do you want to save this sample?");

                                                while (true){
                                                    if (yes) {
                                                        //recordSign(sampleListener.returnOneSample(), signName, sign, allsign);
                                                        sign = new Sign(signName, new Sample(sampleListener.returnOneSample()));
                                                        //ToDo: handle the boolean return value aftter adding the given sign
                                                        allSigns.addSign(signName, sign);
                                                        db.addSign(sign);
                                                        updateMessage("New gesture " + signName + " is created.");
                                                        gestures.add(signName);
                                                        break;
                                                    } else if (no){
                                                        updateMessage("This gesture is not saved.");
                                                        break;
                                                    }
                                                }
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

                                return null;
                            }
                        };
                    }
                };

                newThread.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent event) {
                        message.textProperty().unbind();
                        //message.textProperty().bind(dtwThread.messageProperty());
                    }
                });

                message.textProperty().bind(newThread.messageProperty());
                newThread.start();
            }
        }
    }

    @FXML
    protected void trainButtonAction(ActionEvent event) {
        if (!recording) {
            recording = true;
            String trainName = (String) gestureList.getSelectionModel().getSelectedItem();

            if (trainName == null) {
                // Detect if any gesture is selected
                message.setText("Please select a gesture.");
                recording = false;
            } else {
                Service<Void> trainThread = new Service<Void>() {
                    @Override
                    protected Task<Void> createTask() {
                        return new Task<Void>() {
                            @Override
                            protected Void call() throws Exception {
                                //message.textProperty().unbind();
                                Sign sign;

                                for (int count = 3; count > 0; count--) {
                                    try {
                                        if (count != 1)
                                            updateMessage("The training of gesture " + trainName + " will start in " + count + " seconds.");
                                        else
                                            updateMessage("The training of gesture " + trainName + " will start in " + count + " second.");
                                        Thread.sleep(1000);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                updateMessage("The training of gesture " + trainName + " started.");

                                // recordingMode = true;


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
                                                // Reset the variables
                                                yes = false;
                                                no = false;

                                                // Set the buttons visible
                                                btnYes.setVisible(true);
                                                btnNo.setVisible(true);

                                                updateMessage("Do you want to save this sample?");

                                                while (true){
                                                    if (yes) {
                                                        //trainOneSign(sampleListener.returnOneSample(), trainName, allsign);
                                                        //ToDo: handle the boolean return type after adding sample to the given sign
                                                        sign = new Sign(trainName, new Sample(sampleListener.returnOneSample()));
                                                        allSigns.addSign(trainName, sign);
                                                        db.addSign(sign);
                                                        updateMessage("Training of gesture " + trainName + " completed.");
                                                        break;
                                                    } else if (no){
                                                        updateMessage("This sample is not saved.");
                                                        break;
                                                    }
                                                }
                                            } else {
                                                updateMessage("The recording is invalid.");
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

                                recording = false;
                                return null;
                            }


                        };
                    }
                };

                trainThread.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent event) {
                        message.textProperty().unbind();
                        //message.textProperty().bind(dtwThread.messageProperty());
                    }
                });

                message.textProperty().bind(trainThread.messageProperty());
                trainThread.start();
            }
        }
    }

    @FXML
    protected void infoButtonAction(ActionEvent event){
        String signName = (String) gestureList.getSelectionModel().getSelectedItem();

        // Detect if any gesture is selected
        if (signName == null) {
            message.setText("Please select a gesture.");
            return;
        }

        Sign sign = allSigns.getSign(signName);

        message.setText("Gesture Name: " + sign.getName()
                + " Sample Size: " + sign.getAllSamples().size()
                + " Palm Count: " + sign.getInitialPalmCount()
                + " Hand Type: " + sign.getInitialHandType()
                + " Finger Count: " + sign.getInitialFingerCount());
    }

    @FXML
    protected void removeButtonAction(ActionEvent event) {
        String signName = (String) gestureList.getSelectionModel().getSelectedItem();

        // Detect if any gesture is selected
        if (signName == null) {
            message.setText("Please select a gesture.");
            return;
        }

        // Reset the variables
        yes = false;
        no = false;

        // Set the buttons visible
        btnYes.setVisible(true);
        btnNo.setVisible(true);

        Service<Void> removeThread = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        updateMessage("Are you sure to remove gesture " + signName + "?");

                        while (true){
                            if (yes){
                                try {
                                    Sign sign = db.getSignsByName(signName);
                                    db.removeSign(sign);
                                    allSigns.removeSign(signName);
                                    gestures.remove(signName);
                                    updateMessage("Gesture " + signName + " is removed.");
                                } catch (Exception e) {
                                    updateMessage("Exception caught!");
                                    e.printStackTrace();
                                }
                                break;
                            } else if (no){
                                updateMessage("Gesture " + signName + " is not removed.");
                                break;
                            }

                        }

                        return null;
                    }
                };
            }
        };

        removeThread.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                message.textProperty().unbind();
            }
        });

        message.textProperty().bind(removeThread.messageProperty());
        removeThread.start();
    }

    @FXML
    protected void systemButtonAction(ActionEvent event) {
        // Show or hide the system menu
        if(systemBox.isVisible())
            systemBox.setVisible(false);
        else
            systemBox.setVisible(true);
    }

    @FXML
    protected void resetButtonAction(ActionEvent event){
        // Reset the variables
        yes = false;
        no = false;

        // Set the buttons visible
        btnYes.setVisible(true);
        btnNo.setVisible(true);

        Service<Void> resetThread = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        updateMessage("Are you sure you want to reset the database?");
                        while (true){
                            if (yes){
                                try{
                                    db.removeAllSign();
                                    allSigns.removeAllSign();
                                    gestures.removeAll();
                                    // TODO: BUG: the listview is not updated

                                    updateMessage("Database has been reset.");
                                    break;
                                }catch(Exception e){
                                    e.printStackTrace();
                                }
                            }else if (no){
                                updateMessage("The reset has been cancelled.");
                                break;
                            }
                        }

                        return null;
                    }
                };
            }
        };

        resetThread.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                message.textProperty().unbind();
            }
        });

        message.textProperty().bind(resetThread.messageProperty());
        resetThread.start();
    }

    @FXML
    protected void settingButtonAction(ActionEvent event){
        message.setText("Open setting!");
    }

    @FXML
    protected void switchButtonAction(ActionEvent event){
        if (recognition){
            recognition = false;

            dtwThread.cancel();
            message.setText("The recognition mode is switched off.");
        } else {
            recognition = true;

            message.textProperty().bind(dtwThread.messageProperty());
            dtwThread.start();
        }

    }

    @FXML
    protected void yesButtonAction(ActionEvent event){
        yes = true;
        btnYes.setVisible(false);
        btnNo.setVisible(false);
    }

    @FXML
    protected void noButtonAction(ActionEvent event){
        no = true;
        btnYes.setVisible(false);
        btnNo.setVisible(false);
    }
}
