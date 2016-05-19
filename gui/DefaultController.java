package gui;
/**
 * Created by alex on 10/3/2016.
 *
 * This class handles the GUI interactions.
 * For other operations, please goto gui.GUI.java
 */

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Callback;
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
    @FXML private Button modeButton;
    @FXML private TextFlow loggingArea,dtwTextFlow;
    @FXML public Group mainVisualiser,dtwVisualiser,translateVisualiser,quizVisualiser;
    @FXML private Tab controlTab,loggingTab,dtwTab,translateTab,quizTab;
    @FXML private ScrollPane dtwScrollPane,loggingScrollPane;
    @FXML private Label modeLabel;
    @FXML private Label transModeLabel;
    @FXML private Label correctNumLabel, wrongNumLabel;
    @FXML private TextField translateTextField;
    @FXML private TextField quizTextField;

    private Stage countdown = new Stage();
    private Stage about = new Stage();
    private Stage help = new Stage();

    /* Communication to GUI instance */
    private GUI application;
    private DefaultController myself;

    private Boolean englishMode = true;
    private Boolean answered = true;
    private String answer;

    @Override
    public void initialize(URL location, ResourceBundle resources){
        myself=this;

        modeLabel.setText(String.valueOf(Mode.WordMode));
        modeLabel.setDisable(false);

        //about control tab
        setList(false);
        mainVisualiser.getChildren().add(application.mainVisualiser.getSubScene());
        MenuItem playback=new MenuItem("Playback");
        MenuItem delete=new MenuItem("Delete");
        playback.setOnAction(event -> playback());
        delete.setOnAction(event -> Platform.runLater(() -> {
            Alert alert=new Alert(Alert.AlertType.CONFIRMATION,"Confirm to delete?");
            alert.setResultConverter(param -> {
                if(param==ButtonType.OK){
                    try {
                        application.deleteGesture(getItemSelected());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return null;
            });
            alert.showAndWait();
        }));
        ContextMenu rightClickMenu=new ContextMenu(playback, delete);

        gestureList.setContextMenu(rightClickMenu);
        controlTab.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(!oldValue&&newValue){//when the user clicks the controlTab
                application.startMainVisualizer();
            }else if(oldValue&&!newValue){//when the user leaves the controlTab
                application.stopMainVisualizer();
                application.stopReplay();
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

        //Translate tab
        translateVisualiser.getChildren().add(application.translateVisualiser.getSubScene());
        translateTab.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(!oldValue&&newValue){//when the user clicks the playback tab
                //application.startRecognition();
                application.startTranslateVisualizer();
            }else if(oldValue&&!newValue){//when the user leaves the playback tab
                //application.stopRecognition();
                application.stopTranslateVisualizer();
                application.stopTranslate();
            }
        });

        //Quiz tab
        quizVisualiser.getChildren().add(application.quizVisualiser.getSubScene());
        quizTab.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(!oldValue&&newValue){//when the user clicks the playback tab
                //application.startRecognition();
                application.startQuizVisualizer();
            }else if(oldValue&&!newValue){//when the user leaves the playback tab
                //application.stopRecognition();
                application.stopQuizVisualizer();
                application.stopTranslate();
            }
        });

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
            log(LoggingTemplate.getModeMessage(Mode.SentenceMode));
            startBtnSetText("Stop");
            application.startRecognition();
        }else{
            modeLabel.setText(String.valueOf(Mode.WordMode));
            log(LoggingTemplate.getModeMessage(Mode.WordMode));
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
        time.setFont(Font.font(20));
        message.setFont(Font.font(20));
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

    public void resetButtonAction(){
        Alert alert=new Alert(Alert.AlertType.CONFIRMATION,"Confirm to reset the database?");
        alert.setResultConverter(param -> {
            if(param == ButtonType.OK){
                try {
                    application.resetDatabase();
                    log("The database have been reset.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            log("The reset of the database has been cancelled.");
            return null;
        });
        alert.showAndWait();
    }

    public void helpButtonAction(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("help.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            help.setScene(scene);
            help.setAlwaysOnTop(true);
            help.show();
        }catch(Exception ex){
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void aboutButtonAction(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("about.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            about.setScene(scene);
            about.setAlwaysOnTop(true);
            about.show();
        }catch(Exception ex){
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void translateButtonAction(){
        String message = translateTextField.getText();
        String[] words;

        if(englishMode){
            if(message == ""){
                Platform.runLater(() -> (new Alert(Alert.AlertType.ERROR,"Please input some characters.")).show());
                return;
            }

            if(message.contains(" ")){
                words = message.split(" ");
                try{
                    Platform.runLater(() -> application.translateEngVis(words));
                }catch(Exception e){
                    e.printStackTrace();
                }

            } else {
                Platform.runLater(() -> application.translateEngVis(message));
            }
        }else{
            words = message.split("");
            Platform.runLater(() -> application.translateChiVis(words));
        }

        log(message + " is translated");
    }

    @FXML
    public void transModeButtonAction(){
        if (englishMode){
            transModeLabel.setText("Chinese Mode");
            englishMode = false;
        }else{
            transModeLabel.setText("English Mode");
            englishMode = true;
        }
    }

    @FXML
    public void playButtonAction(){
        if(answered){
            String signName = application.getRandomSign();
            Platform.runLater(() -> application.quizReplayVis(signName));
            answer = new String(signName);
            answered = false;
        }else{
            Platform.runLater(() -> application.quizReplayVis(answer));
        }
    }

    @FXML
    public void answerButtonAction(){
        int correctNumber = Integer.parseInt(correctNumLabel.getText());
        int wrongNumber = Integer.parseInt(wrongNumLabel.getText());

        if(quizTextField.getText().equals(answer)){
            correctNumLabel.setText("" + ++correctNumber);
            Platform.runLater(() -> (new Alert(Alert.AlertType.INFORMATION,"Your answer is correct!")).show());
        }else{
            wrongNumLabel.setText("" + ++wrongNumber);
            Platform.runLater(() -> (new Alert(Alert.AlertType.INFORMATION,"The correct answer is " + answer )).show());
        }

        quizTextField.clear();
        answered = true;
    }

    @FXML
    public void skipButtonAction(){
        int wrongNumber = Integer.parseInt(wrongNumLabel.getText());
        wrongNumLabel.setText("" + ++wrongNumber);

        String signName = application.getRandomSign();
        Platform.runLater(() -> application.quizReplayVis(signName));
        answer = new String(signName);
        answered = false;
    }

    private void playback() {
        Platform.runLater(() -> application.replayVis(getItemSelected()));
    }

    public String getItemSelected(){
        return String.valueOf(gestureList.getSelectionModel().getSelectedItem());
    }
}
