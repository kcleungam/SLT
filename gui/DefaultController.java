package gui;
/**
 * Created by alex on 10/3/2016.
 *
 * This class handles the GUI interactions.
 * For other operations, please goto gui.NewInterface.java
 */

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Callback;
import main.DTW;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultController implements Initializable{
    /* GUI */
    @FXML private ListView<String> gestureList;
    @FXML private TextField inputField;
    @FXML private Button addButton;
    @FXML private TextFlow loggingArea,dtwTextFlow;
    @FXML private SubScene visualiser;
    @FXML private Group mainVisualiser,dtwVisualiser;
    @FXML private Tab controlTab,loggingTab,dtwTab;
    @FXML private ScrollPane dtwScrollPane,loggingScrollPane;
    private Stage countdown=new Stage();

    /* Communication to NewInterface instance */
    private NewInterface application;
    private int countdownTime;
    private DefaultController myself;



    @Override
    public void initialize(URL location, ResourceBundle resources){
        myself=this;

        //about control tab
        setList(false);
        mainVisualiser.getChildren().add(application.mainVisualiser.getSubScene());

        //about DTW tab
        dtwVisualiser.getChildren().add(application.dtwVisualiser.getSubScene());
        dtwTab.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(!oldValue&&newValue){//when the user clicks the DTW tab
                    application.startRecognition();
                }else if(oldValue&&!newValue){//when the user leaves the DTW tab
                    application.stopRecognition();
                }
            }
        });
        dtwScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        dtwScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        //logging tab
        dtwScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        dtwScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        //logging message
        log("OK");
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
        loggingScrollPane.setVvalue(1.0);
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
        loggingScrollPane.setVvalue(1.0);
    }

    public void dtwDisplay(String result){
        Text time=new Text((new Date()).toString()+"\t\t");
        Text message=new Text(result);
        if(result.equals(DTW.UNKNOWN)){
            message.setText(result+"\n");
            message.setFill(Color.RED);
            dtwTextFlow.getChildren().addAll(time,message);
        }else{
            message.setFill(Color.BLUE);
            Text text=new Text("\tis the most similar gesture.\n");
            dtwTextFlow.getChildren().addAll(time,message,text);
        }
        dtwScrollPane.setVvalue(1.0);
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
            countdown.setOnCloseRequest(event -> {
                event.consume();//prevent from closure
            });

            countdown.show();
        }catch(Exception ex){
            Logger.getLogger(NewInterface.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void closeCountdown(){
        countdown.close();
        application.addSign(inputField.getText());

        setList(false);//update the list
    }
}
