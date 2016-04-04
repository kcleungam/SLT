package gui.visualizer;

import com.leapmotion.leap.Controller;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.SampleListener;

public class Test extends Application{

	static Controller controller = new Controller();
	static SampleListener listener = new SampleListener();

	VisualiseFX test = new VisualiseFX(800,600,600);

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		Group root = new Group();
		Scene scene = new Scene(root, 800, 600);
		root.getChildren().add(test.getSubScene());

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Thread th = new Thread(VisTracing);
				th.setDaemon(true);
				th.start();
			}
		});

		primaryStage.setScene(scene);
		primaryStage.setTitle("SLT");
		primaryStage.show();

		System.out.println(test.appEnd[0]);
	}

	Task<Void> VisTracing = new Task<Void>() {
		@Override
		protected Void call() throws Exception {
			while (true) {
				try {
					test.traceLM(controller.frame());
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};

}
