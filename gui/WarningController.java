package gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by ylcheung on 10/4/2016.
 */
public class WarningController extends VBox implements Initializable {

    @FXML private Button yesBtn;
    @FXML private Button noBtn;
    @FXML private Label warningMessage;

    /* Communicate with other controllers */
    private DefaultController defaultController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        warningMessage.setText("Do you want to delete gesture "+ defaultController.getListSelected() +"?");
//        try{
//            FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("gui.fxml"));
//            fxmlLoader.setControllerFactory(new Callback<Class<?>, Object>() {
//                @Override
//                public Object call(Class<?> param) {
//                    defaultController=new DefaultController();
//                    defaultController.setApp(myself);
//                    return defaultController;
//                }
//            });
//
//            Parent root=fxmlLoader.load();
//            Scene scene=new Scene(root, 1280, 800);
//            stage.setScene(scene);
//            stage.setTitle("Sign Language Translator");
//            stage.show();
//            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
//                public void handle(WindowEvent we) {
//                    System.exit(0);
//                }
//            });
//        }catch(Exception ex){
//            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    @FXML public void yesBtnAction(){
        defaultController.resolveWarning(true);
    }

    @FXML public void noBtnAction(){
        defaultController.resolveWarning(false);
    }

    public void setApp(DefaultController defaultController){this.defaultController=defaultController;}

}
