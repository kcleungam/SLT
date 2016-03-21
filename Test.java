//package visualizer;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Test extends Application{

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		VisualiseFX test = new VisualiseFX();
		Group root = new Group();
		Scene scene = new Scene(root, 800, 600);
		root.getChildren().add(test.getSubScene());
		
		primaryStage.setScene(scene);
		primaryStage.setTitle("SLT");
		primaryStage.show();
	}

}
