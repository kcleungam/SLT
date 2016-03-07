package UI;/**
 * Created by alex on 7/3/2016.
 */

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.IOException;

public class NewInterface extends Application {
    @FXML private javafx.scene.control.ListView<String> gestureList;
    @FXML private TextField inputField;
    @FXML private Button addButton;
    @FXML private TextFlow loggingArea;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root=FXMLLoader.load(getClass().getResource("gui.fxml"));
        Scene scene=new Scene(root, 600, 400);
        primaryStage.setTitle("Sign Language Translator");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @FXML
    public void addButtonAction(){

    }
}
