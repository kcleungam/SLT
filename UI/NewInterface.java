package UI;
/**
 * Created by alex on 7/3/2016.
 *
 * This class only invokes the JavaFX main application's thread and manage the operations of database , Leap Motion controller and gesture recognition.
 * For GUI interaction, please goto UI.DefaultController.java
 *
 * The attributes set to be static because only one sign language translator can work at a time..
 * Our project does not intend to support input from more than one Leap Motion controller at a time.
 */

import com.leapmotion.leap.Controller;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.util.Callback;
import main.*;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NewInterface extends Application{
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
    private  Service<Void> dtwThread;

    /* GUI control */
    private static Stage stage;
    private NewInterface myself;



    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //initialise the things related to Leap Motion controller
        sampleListener.lostFocus();
        controller.addListener(sampleListener);
        if(!controller.isConnected()){
            //check whether the controller is connected
            new Alert(Alert.AlertType.ERROR,"Leap Motion controller not found.").show();
            primaryStage.close();
        }
        allSigns=new SignBank(db);
        gestures.addAll(allSigns.getAllSigns().keySet());

        //store the states
        stage=primaryStage;
        myself=this;

        //initialize the controller
        try{
            FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("gui.fxml"));
            fxmlLoader.setControllerFactory(new Callback<Class<?>, Object>() {
                @Override
                public Object call(Class<?> param) {
                    DefaultController product=new DefaultController();
                    product.setApp(myself);
                    return product;
                }
            });
            Parent root=fxmlLoader.load();
            Scene scene=new Scene(root, 600, 400);
            stage.setScene(scene);
            stage.setTitle("Sign Language Translator");
            stage.show();
        }catch(Exception ex){
            Logger.getLogger(NewInterface.class.getName()).log(Level.SEVERE, null, ex);
        }
    }



    /* helper functions for other controllers */

    /**
     * tget gesture names
     * @param sort      true: sorted by ascending order     false: no sorting
     * @return  all gesture names
     */
    public ObservableList<String> getGestures(boolean sort){return (sort)?gestures:gestures.sorted();}
}
