package gui;
/**
 * Created by alex on 10/3/2016.
 *
 * This class handles the GUI interactions.
 * For other operations, please goto gui.GUI.java
 */

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import main.DTW;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultController implements Initializable{
    /* GUI */
    @FXML private ListView<String> gestureList;
    @FXML private TextField inputField;
    @FXML private Button addButton;
    @FXML private Button startButton;
    @FXML private MenuItem modeButton;
    @FXML private TextFlow loggingArea,dtwTextFlow;
    @FXML public Group mainVisualiser,dtwVisualiser;
    @FXML private Tab controlTab,loggingTab,dtwTab;
    @FXML private ScrollPane dtwScrollPane,loggingScrollPane;
    @FXML private Label modeLabel;
    private Stage countdown=new Stage();

    /* Communication to GUI instance */
    private GUI application;
    private DefaultController myself;

    @Override
    public void initialize(URL location, ResourceBundle resources){
        myself=this;

        modeLabel.setText(String.valueOf(Mode.WordMode));

        //about control tab
        setList(false);
        mainVisualiser.getChildren().add(application.mainVisualiser.getSubScene());
        MenuItem playback=new MenuItem("Playback");
        playback.setOnAction(event -> playback(String.valueOf(gestureList.getSelectionModel().getSelectedItems())));
        ContextMenu rightClickMenu=new ContextMenu(playback);
        gestureList.setContextMenu(rightClickMenu);
        controlTab.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(!oldValue&&newValue){//when the user clicks the controlTab
                application.startMainVisualizer();
            }else if(oldValue&&!newValue){//when the user leaves the controlTab
                application.stopMainVisualizer();
            }
        });

        //about DTW tab
        dtwVisualiser.getChildren().add(application.dtwVisualiser.getSubScene());
        dtwTab.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(!oldValue&&newValue){//when the user clicks the DTW tab
                application.startRecognition();
                application.startDtwVisualizer();
            }else if(oldValue&&!newValue){//when the user leaves the DTW tab
                application.stopRecognition();
                application.stopDtwVisualizer();
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

    public void setApp(GUI app){application=app;}

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

    @FXML
    public void modeButtonAction(){
        if (modeLabel.getText()==String.valueOf(Mode.WordMode)){
            modeLabel.setText(String.valueOf(Mode.SentenceMode));
            startBtnSetText("Stop");
            application.startRecognition();
        }else{
            modeLabel.setText(String.valueOf(Mode.WordMode));
            startBtnSetText("Start");
        }
    }

    public String getMode(){
        return modeLabel.getText();
    }

    /**
     * logging plain text in logging tab
     * @param message
     */
    public void log(String message){//create a new line for your each time
        Text text=new Text((new Date()).toString()+"\t"+message+"\n");
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

    public void log(ArrayList<Text> stream){
        stream.forEach(text -> loggingArea.getChildren().add(text));
        loggingScrollPane.setVvalue(1.0);
    }

    public void dtwDisplay(String result){
        Thread speechThread=new Thread(() -> {
            try {
                Speech.play(result);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Text time=new Text((new Date()).toString()+"\t\t");
        Text message=new Text(result+"\n");
        if(result.equals(DTW.UNKNOWN)){
            message.setText("Ready\n");
            dtwTextFlow.getChildren().addAll(time,message);
        }else{
            speechThread.start();
            message.setFill(Color.BLUE);
            dtwTextFlow.getChildren().addAll(time,message);
        }
        dtwScrollPane.setVvalue(1.0);
    }

    @FXML
    public void dtwStartAction(){
        if (startButton.getText()=="Stop"){
            application.stopRecognition();
            startBtnSetText("Start");
        }else{
            startBtnSetText("Stop");
            invokeCountdown();
        }
    }

    public void startBtnSetText(String text){
        startButton.setText(text);
    }

    private void invokeCountdown(){
        try{//load the countdown windows from fxml file
            FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("countdown.fxml"));
            fxmlLoader.setControllerFactory(param -> {
                CountdownController product=new CountdownController();
                product.setApp(myself);
                return product;
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
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void closeCountdown(){
        countdown.close();
        if (dtwTab.isSelected()){
            application.startRecognition();
        }else if (controlTab.isSelected()){
            String input=inputField.getText();
            if(Speech.validate(input)) {
                application.addSign(input);
                setList(false);//update the list
            }
            else{
                Platform.runLater(() -> (new Alert(Alert.AlertType.ERROR,"Please input a proper name contains English or Chinese characters.")).show());
            }
        }

    }

    private void playback(String name){
        Platform.runLater(() -> application.replayVis(gestureList.getSelectionModel().getSelectedItem()));
    }
}
