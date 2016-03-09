package UI;/**
 * Created by alex on 7/3/2016.
 */

import com.leapmotion.leap.Controller;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import main.*;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class NewInterface extends Application implements Initializable {
    /* GUI */
    @FXML private javafx.scene.control.ListView<String> gestureList;
    @FXML private TextField inputField;
    @FXML private Button addButton;
    @FXML private TextFlow loggingArea;
    //private Stage primaryStage;
    //private Scene add_train,counting;

    /* Other components */
    private static Database db = new Database("Signs", "HK_Signs");
    private static SignBank allSigns;
    private SampleListener sampleListener = new SampleListener();
    private Controller controller = new Controller();
    private DTW dtw = new DTW();
    private SLT slt=new SLT();
    private static boolean recording = false;
    private static boolean recognition = false;
    private static volatile boolean yes = false, no = false;
    private ObservableList<String> gestures= FXCollections.observableArrayList();
    private Service<Void> dtwThread;

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //create the default scene: add/train a gesture
        Parent root=FXMLLoader.load(getClass().getResource("gui.fxml"));
        Scene scene=new Scene(root, 600, 400);


        primaryStage.setTitle("Sign Language Translator");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        controller.addListener(sampleListener);
        try {
            allSigns = new SignBank(db);
        } catch (Exception e) {
            System.err.println("Failed to initialize Sign Bank.");
            e.printStackTrace();
        }
        gestures = FXCollections.observableArrayList();
        gestures.addAll(allSigns.getAllSigns().keySet().stream().collect(Collectors.toList()));
        gestureList.setItems(gestures);


    }

    @FXML
    public void addButtonAction(){
        
    }
}
