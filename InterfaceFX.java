/**
 * Created by Luke on 27/1/2016.
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class InterfaceFX extends Application{

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("interfaceFX.fxml"));
        primaryStage.setTitle("Sign Language Translator");
        primaryStage.setScene(new Scene(root, 1200, 700));
        primaryStage.show();
    }

}
