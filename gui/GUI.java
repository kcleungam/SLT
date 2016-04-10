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
import com.leapmotion.leap.Frame;
import data.OneFrame;
import gui.visualizer.VisualiseFX;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
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
    private static ObservableList<String> gestures=FXCollections.observableArrayList();
    private Service<String> dtwService;
    private final int dtwTolerance=1;

    /* GUI control */
    private static Stage stage;
    private GUI myself;
    private DefaultController defaultController;

    /* Visualiser */
    private Service<Void> mainVisService;
    private Service<Void> replayVisService;
    private Service<Void> dtwVisService;
    public VisualiseFX mainVisualiser;
    public VisualiseFX dtwVisualiser;

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //initialise the things related to Leap Motion controller
        sampleListener.setReady(false);
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
        mainVisualiser = new VisualiseFX(1000,760,800);
        dtwVisualiser=new VisualiseFX(1280,570,900);

        dtwVisService = new Service<Void>() {
            @Override
            protected Task createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        while (true) {
                            try {
                                dtwVisualiser.traceLM(controller.frame());
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                //redraw again as the interruption will make the update of some components stop
                                dtwVisualiser.root.getChildren().clear();
                                dtwVisualiser.initializeParam();
                            }
                        }
                    }

                    @Override protected void failed(){
                        super.failed();
                        defaultController.log("mainVis failed.");
                        dtwVisualiser.root.getChildren().clear();
                        dtwVisualiser.initializeParam();
                        restart();
                    }

                    @Override protected void cancelled(){
                        defaultController.log("mainVis cancelled.");
                        dtwVisualiser.root.getChildren().clear();
                        dtwVisualiser.initializeParam();
                    }
                };
            }
        };
        dtwVisService.cancel();

        mainVisService = new Service<Void>() {
            @Override
            protected Task createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        while (true) {
                            try {
                                mainVisualiser.traceLM(controller.frame());
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                //redraw again as the interruption will make the update of some components stop
                                mainVisualiser.root.getChildren().clear();
                                mainVisualiser.initializeParam();
                            }
                        }
                    }

                    @Override protected void failed(){
                        super.failed();
                        defaultController.log("mainVis failed.");
                        mainVisualiser.root.getChildren().clear();
                        mainVisualiser.initializeParam();
                        restart();
                    }

                    @Override protected void cancelled(){
                        defaultController.log("mainVis cancelled.");
                        mainVisualiser.root.getChildren().clear();
                        mainVisualiser.initializeParam();
                    }
                };
            }
        };
        mainVisService.start();

        dtwService=new Service<String>() {
            @Override
            protected Task<String> createTask() {
                return new Task<String>() {
                    @Override
                    protected String call() throws Exception {
                        //capture the gesture first
                        sampleListener.reset();
                        sampleListener.setReady(true);
                        defaultController.startBtnSetText("Stop");

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

                                //terminate
                                sampleListener.setReady(false);
                                Thread.yield();
                                break;
                            }
                            //release the thread for a while
                            try {
                                Thread.currentThread().sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

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

                            //release current thread
                            try{
                                Thread.currentThread().sleep(1000);
                            }catch (InterruptedException e){
                                e.printStackTrace();
                            }

                            dtw.reset();//re-initialise for next time
                            return result;
                        }
                        return null;
                    }

                    @Override protected void succeeded(){
                        super.succeeded();
                        if(dtwService.getValue()!=null){
                            defaultController.dtwDisplay(dtwService.getValue());
                        }
                        if(defaultController.getMode()=="WordMode"){
                            defaultController.startBtnSetText("Start");
                        }else if(defaultController.getMode()=="SentenceMode"){
                            dtwService.restart();
                        }
                    }
                };
            }
        };
        dtwService.cancel();

        //initialize the controllers of interface
        try{
            FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("gui.fxml"));
            fxmlLoader.setControllerFactory(param -> {
                defaultController=new DefaultController();
                defaultController.setApp(myself);
                return defaultController;
            });

            Parent root=fxmlLoader.load();
            Scene scene=new Scene(root, 1280, 800);
            stage.setScene(scene);
            stage.setTitle("Sign Language Translator");
            stage.show();
            stage.setOnCloseRequest(we -> System.exit(0));
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
        sampleListener.setReady(true);

        //start recording
        Thread addSignThread=new Thread(() -> {
            while(true){
                if(sampleListener.checkFinish()){
                    if(sampleListener.checkValid()){
                        Platform.runLater(() -> {
                            Alert alert=new Alert(Alert.AlertType.CONFIRMATION,"Confirm to store?");
                            //if user wants to store the gesture
                            alert.setResultConverter(param -> {
                                if(param==ButtonType.OK){
                                    //add to the database, sign bank and gestures
                                    try {
                                        ArrayList<Frame> arr=sampleListener.returnOneSample();

                                        Sign sign=new Sign(name,new Sample(arr));
                                        allSigns.addSign(name,sign);
                                        db.addSign(sign);

                                        //update logging
                                        defaultController.log(LoggingTemplate.getUpdateMessage(name,arr.size(),LoggingTemplate.UPDATE.ADD));

                                        if(!gestures.contains(name))
                                            gestures.add(name);//add name if not exist
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                return null;
                            });
                            alert.showAndWait();
                        });
                    }else{//invalid samples
                        Platform.runLater(() -> (new Alert(Alert.AlertType.WARNING,"Invalid recording. Too few samples.")).showAndWait());
                    }

                    //terminate after the input is determined to be finished
                    sampleListener.setReady(false);
                    Thread.yield();
                    break;
                }

                //release this thread for a while
                try {
                    Thread.currentThread().sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        addSignThread.start();
    }

    public void replayVis(String gestureName) {
        replayVisService = new Service<Void>() {
            @Override
            protected Task createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        Sample sample=db.getFirstSample(gestureName);
                        for (OneFrame i:sample.getAllFrames()) {
                            try {
                                mainVisualiser.traceLM(i);
                                Thread.sleep(100);
                            } catch (Exception e) {
                                e.printStackTrace();
                                //redraw again as the interruption will make the update of some components stop
                                mainVisualiser.root.getChildren().clear();
                                mainVisualiser.initializeParam();
                            }
                        }
                        return null;
                    }

                    @Override protected void running(){
                        super.running();
                        mainVisService.cancel();
                    }

                    @Override protected void failed(){
                        super.failed();
                        defaultController.log("replay failed.");
                    }

                    @Override protected void scheduled(){
                        super.scheduled();
                        if(mainVisService!=null||mainVisService.isRunning())
                            mainVisService.cancel();
                    }

                    @Override protected void succeeded(){
                        super.succeeded();
                        mainVisualiser.root.getChildren().clear();
                        mainVisualiser.initializeParam();
                        mainVisService.restart();
                    }
                };
            }
        };
//        mainVisService.cancel();
        replayVisService.start();
    }

    public void startRecognition(){
        dtwService.restart();
    }

    public void stopRecognition(){
        if(dtwService!=null||dtwService.isRunning())
            dtwService.cancel();
    }

    public void startMainVisualizer(){
        mainVisService.restart();
    }

    public void stopMainVisualizer(){
        if(mainVisService!=null||mainVisService.isRunning())
            mainVisService.cancel();
    }

    public void startDtwVisualizer(){
        dtwVisService.restart();
    }

    public void stopDtwVisualizer(){
        if(dtwVisService!=null||dtwVisService.isRunning())
            dtwVisService.cancel();
    }

    public void deleteGesture(String deleteGest) throws Exception {
        if(db.removeSign(db.getSignsByName(deleteGest))){
            defaultController.setList(false);
            defaultController.log(deleteGest + "deleted.");
        }
    }
}
