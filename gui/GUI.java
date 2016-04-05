package gui;
/**
 * Created by alex on 7/3/2016.
 *
 * This class only invokes the JavaFX main application's thread and manage the operations of database , Leap Motion controller and gesture recognition.
 * For GUI interaction, please goto gui.DefaultController.java
 *
 * The attributes set to be static because only one sign language translator can work at a time..
 * Our project does not intend to support input from more than one Leap Motion controller at a time.
 */

import com.leapmotion.leap.Controller;
import data.OneFrame;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import main.*;
import gui.visualizer.VisualiseFX;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GUI extends Application{
    /* Main control components */
    private static Database db = new Database("Signs", "HK_Signs");
    private  SignBank allSigns;
    private  SampleListener sampleListener = new SampleListener();
    private  Controller controller = new Controller();
    private  DTW dtw = new DTW();
    private  SLT slt=new SLT();
    private static boolean recording = false;
    private static boolean recognition = false;
    private static volatile boolean yes = false, no = false;
    private static ObservableList<String> gestures=FXCollections.observableArrayList();
    private Service<String> dtwService;
    private final int dtwTolerance=1;

    /* GUI control */
    private static Stage stage;
    private GUI myself;
    private DefaultController defaultController;

    /* Visualiser */
    public VisualiseFX mainVisualiser = new VisualiseFX(420,346,500);
    public VisualiseFX dtwVisualiser=new VisualiseFX(600,300,500);

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //initialise the things related to Leap Motion controller
//        sampleListener.lostFocus();
        controller.addListener(sampleListener);
        if(!controller.isConnected()){
            //check whether the controller is connected
            new Alert(Alert.AlertType.ERROR,"Leap Motion controller not found.").show();
            primaryStage.close();
        }

        //initialise the things related to the database
        allSigns=new SignBank(db);
        gestures.addAll(allSigns.getAllSigns().keySet());

        //store the states
        stage=primaryStage;
        myself=this;

        //gesture visualize thread
        mainVisualiser.restart();
        //dtwVisualiser.restart();

        //initialize the controllers of interface
        try{
            FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("gui.fxml"));
            fxmlLoader.setControllerFactory(new Callback<Class<?>, Object>() {
                @Override
                public Object call(Class<?> param) {
                    defaultController=new DefaultController();
                    defaultController.setApp(myself);
                    return defaultController;
                }
            });

            Parent root=fxmlLoader.load();
            Scene scene=new Scene(root, 600, 400);
            stage.setScene(scene);
            stage.setTitle("Sign Language Translator");
            stage.show();
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we) {
                    System.exit(0);
                }
            });
        }catch(Exception ex){
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /* helper functions for other controllers */

    /**
     * tget gesture names
     * @param sort      true: sorted by ascending order     false: no sorting
     * @return  all gesture names
     */
    public ObservableList<String> getGestures(boolean sort){return (sort)?gestures:gestures.sorted();}

    public void addSign(String name){

        sampleListener.reset();
//        sampleListener.gainFocus();

        //start recording
        Thread addSignThread=new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    if(sampleListener.checkFinish()){
                        if(sampleListener.checkValid()){
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    Alert alert=new Alert(Alert.AlertType.CONFIRMATION,"Confirm to store?");
                                    //if user wants to store the gesture
                                    alert.setResultConverter(new Callback<ButtonType, ButtonType>() {
                                        @Override
                                        public ButtonType call(ButtonType param) {
                                            if(param==ButtonType.OK){
                                                //add to the database, sign bank and gestures
                                                try {
                                                    Sign sign=new Sign(name,new Sample(sampleListener.returnOneSample()));
                                                    allSigns.addSign(name,sign);
                                                    db.addSign(sign);
                                                    if(!gestures.contains(name))
                                                        gestures.add(name);//add name if not exist
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            return null;
                                        }
                                    });
                                    alert.showAndWait();
                                }
                            });
                        }else{//invalid samples
                            Platform.runLater(() -> (new Alert(Alert.AlertType.WARNING,"Invalid recording. Too few samples.")).showAndWait());
                        }
                        Thread.yield();
                        break;//terminate after the input is determined to be finished
                    }
                    try {
                        Thread.currentThread().sleep(100);//release this thread for a while
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        addSignThread.start();
    }

    public void startRecognition(){
        dtwService=new Service<String>() {
            @Override
            protected Task<String> createTask() {
                return new Task<String>() {
                    @Override
                    protected String call() throws Exception {
                        //capture the gesture first
                        sampleListener.reset();
//                        sampleListener.gainFocus();

                        //try to record the gesture
                        boolean input;
                        Sample source = null;
                        while(true){
                            if(sampleListener.checkFinish()){
                                if(sampleListener.checkValid()){
                                    input=true;
                                    try {
                                        source=new Sample(sampleListener.returnOneSample());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }else{
                                    input=false;
                                }
                                Thread.yield();
                                break;//terminate
                            }
                            //release the thread for a while
                            try {
                                Thread.currentThread().sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
//                        sampleListener.lostFocus();

                        //when successfully get the gesture
                        if(input){
                            DTW dtw=new DTW();
                            dtw.setRSample(source);
                            HashMap<String,Sign> signByBoth = null;
                            try {
                                signByBoth=db.getSignsByBoth(source.getInitialFingerCount(),source.getInitialHandType(),dtwTolerance);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            for(Sign storedSign:signByBoth.values()){
                                dtw.setStoredSign(storedSign);
                                dtw.calDTW();
                            }
                            String result=dtw.getResult();
                            try{
                                defaultController.dtwWait();
                                Thread.currentThread().sleep(1000);
                            }catch (InterruptedException e){
                                e.printStackTrace();
                            }
                            dtw.reset();
                            return result;
                        }
                        return null;
                    }
                };
            }
        };
        dtwService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                if(dtwService.getValue()!=null)
                    defaultController.dtwDisplay(dtwService.getValue());
                dtwService.restart();
            }
        });
        dtwService.start();
    }

    public void replayVis(String gestureName){
        mainVisualiser.cancel();
        ArrayList<OneFrame> replayGesture = allSigns.getSign(gestureName).getFirstSamples().getAllFrames();
        for (OneFrame i:replayGesture) {
            try {
                mainVisualiser.traceLM(i);
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
                //redraw again as the interruption will make the update of some components stop
                mainVisualiser.root.getChildren().clear();
                mainVisualiser.initializeParam();
            }
        }
        mainVisualiser.restart();
    }

    public void stopRecognition(){
        if(dtwService!=null)
            dtwService.cancel();
    }
}
