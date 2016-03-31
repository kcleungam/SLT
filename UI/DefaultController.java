package UI;
/**
 * Created by alex on 10/3/2016.
 *
 * This class handles the GUI interactions.
 * For other operations, please goto UI.NewInterface.java
 */

import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import visualizer.VisualiseFX;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultController implements Initializable{
    /* GUI */
    @FXML private ListView<String> gestureList;
    @FXML private TextField inputField;
    @FXML private Button addButton;
    @FXML private TextFlow loggingArea;
    @FXML private SubScene visualiser;
    @FXML private Group mainVisualiser,dtwVisualiser;
    @FXML private Tab controlTab,loggingTab,dtwTab;
    private Stage countdown=new Stage();

    /* Communication to NewInterface instance */
    private NewInterface application;
    private int countdownTime;
    private DefaultController myself;



    @Override
    public void initialize(URL location, ResourceBundle resources){
        myself=this;
        setList(false);
        log("OK");
        mainVisualiser.getChildren().add(application.mainVisualiser.getSubScene());
        dtwVisualiser.getChildren().add(application.dtwVisualiser.getSubScene());
        System.out.println();
    }

    public void setApp(NewInterface app){application=app;}

    /**
     * set the content of the List View
     * @param sort      true: sorted by ascending order     false: no sorting
     */
    public void setList(boolean sort){
        gestureList.setItems(application.getGestures(sort));
    }



    /* GUI interactions */
    @FXML
    public void addButtonAction(){
        if(inputField!=null&&!inputField.getText().isEmpty()){
            //show countdown timer
           invokeCountdown();
        }else{//require input name
            new Alert(Alert.AlertType.ERROR,"Please input the gesture name first.").show();
        }
    }

    /**
     * logging plain text in logging tab
     * @param message
     */
    public void log(String message){//create a new line for your each time
        Text text=new Text(message+"\n");
        loggingArea.getChildren().add(text);
    }

    /**
     * logging rich text in logging tab
     * @param text
     */
    public void log(Text text){//create a new line for your each time
        String message=text.getText();
        Character lastChar=message.charAt(message.length()-1);
        if(!lastChar.equals('\n'))
            text.setText(message+"\n");
        loggingArea.getChildren().add(text);
    }

    private void invokeCountdown(){
        try{//load the countdown windows from fxml file
            FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("countdown.fxml"));
            fxmlLoader.setControllerFactory(new Callback<Class<?>, Object>() {
                @Override
                public Object call(Class<?> param) {
                    CountdownController product=new CountdownController();
                    product.setApp(myself);
                    return product;
                }
            });
            Parent root=fxmlLoader.load();
            Scene scene=new Scene(root);
            countdown.setScene(scene);
            countdown.setAlwaysOnTop(true);
            countdown.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    event.consume();//prevent from closure
                }
            });
            countdown.show();
        }catch(Exception ex){
            Logger.getLogger(NewInterface.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void closeCountdown(){countdown.close();}

}
