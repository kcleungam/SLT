package visualizer;

import java.util.ArrayList;
import java.util.Arrays;

import com.leapmotion.leap.Bone;
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.HandList;
import com.leapmotion.leap.Vector;
import main.SampleListener;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.*;
import javafx.scene.paint.Color;

public class VisualiseFX{

	static Controller controller = new Controller();
	static SampleListener listener = new SampleListener();

	// constants
	private int viewwidth = 400;
	private int viewheight = 346;
	private int viewdepth = 600;
	private final int fingerSize = 10;
	private final int palmSize = 25;

	// the prog starts from top-left, increase right, down, and inward.
	private final int[] appStart = { 0, 0, 0 };
	private final int[] appEnd = { viewwidth, viewheight, viewdepth };
	private final float[] leapStart = { -200.0f, 0.0f, -200.0f };
	private final float[] leapEnd = { 200.0f, 400.0f, 200.0f };

	// graphic-related
	private Group root;
	private SubScene subScene;

	private VisSphere[][][] fingerNode = new VisSphere[2][5][5];
	private VisSphere[] palmNode = new VisSphere[2];

	private Point3D[][][] fingerCoor = new Point3D[2][5][5]; // [hand][finger][joint]
	private Point3D[] palmCoor = new Point3D[2];

	private PolyLine3D[][] fingerLine = new PolyLine3D[2][5];
	private PolyLine3D[] palmLine = new PolyLine3D[2];

	public VisualiseFX(){
//		this.viewwidth = viewwidth;
//		this.viewheight = viewheight;
		
		buildSubscene();
	}
	
	public void buildSubscene() {

		controller.addListener(listener);

		root = new Group();
		subScene = new SubScene(root, viewwidth, viewheight);
		subScene.setFill(Color.BLACK);

		lightSetting();

		Camera camera = new PerspectiveCamera();
		subScene.setCamera(camera);

		initializeParam();

		Task<Void> task = new Task<Void>() {
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
			}
		};

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Thread th = new Thread(task);
				th.setDaemon(true);
				th.start();
			}
		});

//		stage.setScene(scene);
//		stage.setTitle("SLT");
//		stage.show();

	}

	/*
	 * graphic-related
	 */

	/*
	 * initialize lights used in this app
	 */
	private void lightSetting() {
		// Creating Ambient Light
		AmbientLight ambient = new AmbientLight();
		ambient.setColor(Color.rgb(0, 255, 0, 0.6));
		// Creating Point Light
		PointLight point = new PointLight();
		point.setColor(Color.rgb(255, 255, 255, 1));
		point.setLayoutX(400);
		point.setLayoutY(100);
		point.setTranslateZ(-1100);

		root.getChildren().addAll(ambient, point);

	}

	/*
	 * initialize the spheres
	 */
	public void initializeParam() {
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 5; j++) {
				for (int k = 0; k < 5; k++) {
					fingerCoor[i][j][k] = new Point3D(0, 0, -100);
					fingerNode[i][j][k] = new VisSphere(fingerSize);
					root.getChildren().add(fingerNode[i][j][k]);
				}
				fingerLine[i][j] = new PolyLine3D(getPoint3DArray(i, j), 3, Color.WHITE);
				root.getChildren().add(fingerLine[i][j]);
			}
			palmCoor[i] = new Point3D(0, 0, -100);
			palmNode[i] = new VisSphere(palmSize);
			palmLine[i] = new PolyLine3D(getPoint3DArray(i), 3, Color.WHITE);
			root.getChildren().addAll(palmNode[i], palmLine[i]);
		}
		// for better appearance, some nodes are hidden
		fingerNode[0][1][4].setVisible(false);
		fingerNode[0][2][4].setVisible(false);
		fingerNode[0][3][4].setVisible(false);
		fingerNode[1][1][4].setVisible(false);
		fingerNode[1][2][4].setVisible(false);
		fingerNode[1][3][4].setVisible(false);
		// scene.addChild(lineShape);
	}

	/*
	 * add spheres by coordinates
	 */
	public void addSphere(Point3D coordinate, float size) {
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
				//fingerLine[i][j] = new PolyLine3D(getPoint3DArray(i, j), 3, Color.WHITE);
			}
			//palmLine[i] = new PolyLine3D(getPoint3DArray(i), 3, Color.WHITE);
			palmNode[i].setTranslate(palmCoor[i]);
		}
		
	}

	public void traceLM(Frame frame) {
		setCoor(frame);
		updateGraphic();
	}

	/*
	 * calculation related
	 */

	public ArrayList<Point3D> getPoint3DArray(int palm) {
		return new ArrayList<Point3D>(Arrays.asList(new Point3D[] { fingerCoor[palm][1][3], fingerCoor[palm][2][3],
				fingerCoor[palm][3][3], fingerCoor[palm][4][3], fingerCoor[palm][4][4], fingerCoor[palm][0][4],
				fingerCoor[palm][1][3] }));
	}

	public ArrayList<Point3D> getPoint3DArray(int palm, int finger) {
		return new ArrayList<Point3D>(Arrays.asList(new Point3D[] { fingerCoor[palm][finger][0],
				fingerCoor[palm][finger][1], fingerCoor[palm][finger][2], fingerCoor[palm][finger][3] }));
	}

	/*
	 * set the internal fingerCoor and palmCoor according the LM frame
	 */
	public void setCoor(Frame frame) {
		HandList hands = frame.hands();
		int i = 0;
		// for the existing hands
		for (; 0 <= i && i < hands.count(); i++) {
			for (int j = 0; j < 5; j++) {
				fingerCoor[i][j][0] = new Point3D(rangeConvert(hands.get(i).fingers().get(j).tipPosition()));
				fingerCoor[i][j][1] = new Point3D(
						rangeConvert(hands.get(i).fingers().get(j).bone(Bone.Type.TYPE_DISTAL).prevJoint()));
				fingerCoor[i][j][2] = new Point3D(
						rangeConvert(hands.get(i).fingers().get(j).bone(Bone.Type.TYPE_INTERMEDIATE).prevJoint()));
				fingerCoor[i][j][3] = new Point3D(
						rangeConvert(hands.get(i).fingers().get(j).bone(Bone.Type.TYPE_PROXIMAL).prevJoint()));
				fingerCoor[i][j][4] = new Point3D(
						rangeConvert(hands.get(i).fingers().get(j).bone(Bone.Type.TYPE_METACARPAL).prevJoint()));
			}
			palmCoor[i] = new Point3D(rangeConvert(hands.get(i).palmPosition()));
		}
		// for non-existing hands:
		for (; 0 <= i && i < 2; i++) {
			for (int j = 0; j < 5; j++) {
				for (int k = 0; k < 5; k++) {
					fingerCoor[i][j][k] = new Point3D(0, 0, -100);
				}
			}
			palmCoor[i] = new Point3D(0, 0, -100);
		}
	}

	public float[] rangeConvert(Vector LeapCoor) {
		return rangeConvert(new float[] { LeapCoor.getX(), LeapCoor.getY(), LeapCoor.getZ() });
	}

	public float[] rangeConvert(float[] LeapValue) {
		double[] temp = new double[3];
		for (int i = 0; i < 3; i++) {
			temp[i] = (LeapValue[i] - leapStart[i]) * (appEnd[i] - appStart[i]) / (leapEnd[i] - leapStart[i])
					+ appStart[i];
		}
		float[] appValue = { (float) temp[0], (float) temp[2], (float) temp[1] };

		return appValue;
	}
	
	public float[] normalizer (float[] LeapValue) {
		double[] temp = new double[3];
		for (int i = 0; i < 3; i++) {
			temp[i] = LeapValue[i]+appEnd[i]/2;
		}
		float[] appValue = { (float) temp[0], (float) temp[2], (float) temp[1] };

		return appValue;
	}

	
	public SubScene getSubScene() { return subScene; } 
	
//	public static void main(String[] args) {
//		launch(args);
//	}
}
