package visualpack;

import com.leapmotion.leap.Bone;
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.HandList;
import com.leapmotion.leap.Vector;
import database.SampleListener;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.stage.Stage;

public class VisualiseFX extends Application {

	static Controller controller = new Controller();
	static SampleListener listener = new SampleListener();
	
	// constants
	private final int viewwidth = 800;
	private final int viewheight = 600;
	private final int viewdepth = 600;
	private final int fingerSize = 10;
	private final int palmSize = 25;

	// the prog starts from top-left, increase right, down, and inward.
	private final int[] appStart = { 0, 0, 0 };
	private final int[] appEnd = { viewwidth, viewheight, viewdepth };
	private final float[] leapStart = { -200.0f, 0.0f, -200.0f };
	private final float[] leapEnd = { 200.0f, 400.0f, 200.0f };

	// graphic-related
	private Group root;
	private Scene scene;

	private VisSphere[][][] fingerNode = new VisSphere[2][5][5];
	private VisSphere[] palmNode = new VisSphere[2];

	private VisCoordinate[][][] fingerCoor = new VisCoordinate[2][5][5]; // [hand][finger][joint]
	private VisCoordinate[] palmCoor = new VisCoordinate[2];

	@Override
	public void start(final Stage stage) {

		controller.addListener(listener);
		
		Polyline lines = new Polyline();
		
		root = new Group();
		scene = new Scene(root, viewwidth, viewheight);
		scene.setFill(Color.BLACK);

		lightSetting();

		Camera camera = new PerspectiveCamera();
		scene.setCamera(camera);

		initializeParam();
		
		Task<Void> task = new Task<Void>(){
			@Override
			protected Void call() throws Exception {
			while (true) {
				try {
					traceLM(controller.frame());
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}};
		
		Platform.runLater(new Runnable(){
            @Override
            public void run() {
            	Thread th = new Thread(task);
            	th.setDaemon(true);
            	th.start();
            }}
        );
		
		stage.setScene(scene);
		stage.setTitle("SLT");
		stage.show();
		
	}

	
	
	/*
	 * graphic-related
	 */

	/*
	 * initialize lights used in this app
	 */
	private void lightSetting() {
		// Set up light source
		PointLight light = new PointLight();
		light.setColor(Color.WHITE);

		// Set up the ambient light
		AmbientLight amblight = new AmbientLight();
		amblight.setColor(Color.BLUE);

		Group lightGroup = new Group();
		lightGroup.getChildren().add(light);
		lightGroup.getChildren().add(amblight);
		root.getChildren().add(lightGroup);
		lightGroup.setTranslateX(viewwidth*0.15);
		lightGroup.setTranslateY(viewheight*0.15);
		lightGroup.setTranslateZ(-viewdepth*0.5);
	}

	/*
	 * initialize the spheres
	 */
	public void initializeParam() {
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 5; j++) {
				for (int k = 0; k < 5; k++) {
					fingerNode[i][j][k] = new VisSphere(fingerSize);
					root.getChildren().add(fingerNode[i][j][k]);
				}
			}
			palmNode[i] = new VisSphere(palmSize);
			root.getChildren().add(palmNode[i]);
		}
		// scene.addChild(lineShape);
	}

	/*
	 * add spheres by coordinates
	 */
	public void addSphere(VisCoordinate coordinate, float size) {
		VisSphere sphere = new VisSphere(coordinate, size);
		root.getChildren().add(sphere);
	}

	public void updateGraphic() {
		// update spheres
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 5; j++) {
				for (int k = 0; k < 5; k++) {
					fingerNode[i][j][k].setTranslate(fingerCoor[i][j][k]);
				}
			}
			palmNode[i].setTranslate(palmCoor[i]);
		}
		// update lines
		// geometry.setCoordinates(0, getPointArray());
	}

	public void traceLM(Frame frame) {
		setCoor(frame);
		updateGraphic();
	}

	/*
	 * calculation related
	 */

	/*
	 * set the internal fingerCoor and palmCoor according the LM frame
	 */
	public void setCoor(Frame frame) {
		HandList hands = frame.hands();
		int i = 0;
		// for the existing hands
		for (; 0 <= i && i < hands.count(); i++) {
			for (int j = 0; j < 5; j++) {
				fingerCoor[i][j][0] = new VisCoordinate(rangeConvert(hands.get(i).fingers().get(j).tipPosition()));
				fingerCoor[i][j][1] = new VisCoordinate(
						rangeConvert(hands.get(i).fingers().get(j).bone(Bone.Type.TYPE_DISTAL).prevJoint()));
				fingerCoor[i][j][2] = new VisCoordinate(
						rangeConvert(hands.get(i).fingers().get(j).bone(Bone.Type.TYPE_INTERMEDIATE).prevJoint()));
				fingerCoor[i][j][3] = new VisCoordinate(
						rangeConvert(hands.get(i).fingers().get(j).bone(Bone.Type.TYPE_PROXIMAL).prevJoint()));
				fingerCoor[i][j][4] = new VisCoordinate(
						rangeConvert(hands.get(i).fingers().get(j).bone(Bone.Type.TYPE_METACARPAL).prevJoint()));
			}
			palmCoor[i] = new VisCoordinate(rangeConvert(hands.get(i).palmPosition()));
		}
		// for non-existing hands:
		for (; 0 <= i && i < 2; i++) {
			for (int j = 0; j < 5; j++) {
				for (int k = 0; k < 5; k++) {
					fingerCoor[i][j][k] = new VisCoordinate(0, 0, -100);
				}
			}
			palmCoor[i] = new VisCoordinate(0, 0, -100);
		}
	}

	public double[] rangeConvert(Vector LeapCoor) {
		return rangeConvert(new float[] { LeapCoor.getX(), LeapCoor.getY(), LeapCoor.getZ() });
	}

	public double[] rangeConvert(float[] LeapValue) {
		double[] temp = new double[3];
		for (int i = 0; i < 3; i++) {
			temp[i] = (LeapValue[i] - leapStart[i]) * (appEnd[i] - appStart[i]) / (leapEnd[i] - leapStart[i])
					+ appStart[i];
		}
		double[] appValue = { temp[0], temp[2], temp[1] };

		return appValue;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
