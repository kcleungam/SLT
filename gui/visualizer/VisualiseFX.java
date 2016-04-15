package gui.visualizer;

import com.leapmotion.leap.Bone;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.HandList;
import com.leapmotion.leap.Vector;
import data.*;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;

public class VisualiseFX{

	// constants
	private int viewwidth;	// see gui.fxml
	private int viewheight;	//see gui.fxml
	private int viewdepth;
	private final int fingerSize = 10;
	private final int palmSize = 25;

	// the scene starts from top-left, increase right, down, and inward.

	// graphic-related
	public Group root;
	private SubScene subScene;

	private VisSphere[][][] fingerNode = new VisSphere[2][5][5];
	private VisSphere[] palmNode = new VisSphere[2];

	private Point3D[][][] fingerCoor = new Point3D[2][5][5]; // [hand][finger][joint]
	private Point3D[] palmCoor = new Point3D[2];

	private PolyCylinder3D[][] fingerLine = new PolyCylinder3D[2][5];
	private PolyCylinder3D[] palmLine = new PolyCylinder3D[2];

	public VisualiseFX(int viewWidth,int viewHeight,int viewDepth){
		viewwidth=viewWidth;
		viewheight=viewHeight;
		viewdepth=viewDepth;
		buildSubscene();
	}

	public void buildSubscene() {
		root = new Group();

		subScene = new SubScene(root, viewwidth, viewheight, true, SceneAntialiasing.BALANCED);
		subScene.setFill(Color.TRANSPARENT);

		Camera camera = new PerspectiveCamera();
		subScene.setCamera(camera);

		initializeParam();
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
		lightSetting();

		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 5; j++) {
				for (int k = 0; k < 5; k++) {
					fingerCoor[i][j][k] = new Point3D(0, 0, -100);
					fingerNode[i][j][k] = new VisSphere(fingerSize);
					root.getChildren().add(fingerNode[i][j][k]);
				}
				fingerLine[i][j] = new PolyCylinder3D(getPoint3DArray(i, j), 5, Color.WHITE);
				root.getChildren().addAll(fingerLine[i][j].getLine());
			}
			palmCoor[i] = new Point3D(0, 0, -100);
			palmNode[i] = new VisSphere(palmSize);
			palmLine[i] = new PolyCylinder3D(getPoint3DArray(i), 7, Color.WHITE);
			root.getChildren().addAll(palmLine[i].getLine());
			root.getChildren().add(palmNode[i]);
		}
		// for better appearance, some nodes are hidden
		fingerNode[0][1][4].setVisible(false);
		fingerNode[0][2][4].setVisible(false);
		fingerNode[0][3][4].setVisible(false);
		fingerNode[1][1][4].setVisible(false);
		fingerNode[1][2][4].setVisible(false);
		fingerNode[1][3][4].setVisible(false);
	}

	/*
	 * add spheres by coordinates
	 */
	public void addSphere(Point3D coordinate, float size) {
		VisSphere sphere = new VisSphere(coordinate, size);
		root.getChildren().add(sphere);
	}

	public synchronized void updateGraphic() {
		// update spheres
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 5; j++) {
				for (int k = 0; k < 5; k++) {
					fingerNode[i][j][k].setTranslate(fingerCoor[i][j][k]);
				}
				fingerLine[i][j].update(getPoint3DArray(i,j));
			}
			palmLine[i].update(getPoint3DArray(i));
			palmNode[i].setTranslate(palmCoor[i]);
		}
	}

	public void traceLM(Frame frame) {
		setCoor(frame);
		updateGraphic();
	}

	public void traceLM(OneFrame frame) {
		setCoor(frame);
		updateGraphic();
		try{
			Thread.currentThread().sleep(40);
		} catch (InterruptedException e){
				try {
					Thread.currentThread().join();
				}catch (Exception f) {
					f.printStackTrace();
				}
				e.printStackTrace();
			e.printStackTrace();
		}
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
		for (; i < hands.count(); i++) {
			for (int j = 0; j < 5; j++) {
				fingerCoor[i][j][0] = rangeConvert(hands.get(i).fingers().get(j).tipPosition());
				fingerCoor[i][j][1] =
						rangeConvert(hands.get(i).fingers().get(j).bone(Bone.Type.TYPE_DISTAL).prevJoint());
				fingerCoor[i][j][2] =
						rangeConvert(hands.get(i).fingers().get(j).bone(Bone.Type.TYPE_INTERMEDIATE).prevJoint());
				fingerCoor[i][j][3] =
						rangeConvert(hands.get(i).fingers().get(j).bone(Bone.Type.TYPE_PROXIMAL).prevJoint());
				fingerCoor[i][j][4] =
						rangeConvert(hands.get(i).fingers().get(j).bone(Bone.Type.TYPE_METACARPAL).prevJoint());
			}
			palmCoor[i] = rangeConvert(hands.get(i).palmPosition());
		}
		// for non-existing hands:
		for (; i < 2; i++) {
			for (int j = 0; j < 5; j++) {
				for (int k = 0; k < 5; k++) {
					fingerCoor[i][j][k] = new Point3D(0, 0, -100);
				}
			}
			palmCoor[i] = new Point3D(0, 0, -100);
		}
	}

	public void setCoor(OneFrame Frame){
		PalmData palms = Frame.getPalmData();
		FingerData finger = Frame.getFingerData();
		int i = 0;
		for (; i < palms.getCount(); i++) {
			for (int j = 0; j < 5; j++) {
				fingerCoor[i][j][0] = rangeConvert(finger.coordinates.get(i*5+j));
				fingerCoor[i][j][1] =
						rangeConvert(finger.getDistal().get(i*5+j));
				fingerCoor[i][j][2] =
						rangeConvert(finger.getIntermediate().get(i*5+j));
				fingerCoor[i][j][3] =
						rangeConvert(finger.getProximal().get(i*5+j));
				fingerCoor[i][j][4] =
						rangeConvert(finger.getMetacarpal().get(i*5+j));
			}
			palmCoor[i] = rangeConvert(palms.getCoordinates().get(i));
		}
		// for non-existing hands:
		for (; i < 2; i++) {
			for (int j = 0; j < 5; j++) {
				for (int k = 0; k < 5; k++) {
					fingerCoor[i][j][k] = new Point3D(0, 0, -100);
				}
			}
			palmCoor[i] = new Point3D(0, 0, -100);
		}
	}

	public Point3D rangeConvert(data.Coordinate DataCoor) {
		double[] dataValue = new double[] {DataCoor.getX(), DataCoor.getY(), DataCoor.getZ()};
		return new Point3D (dataValue[0]*2.0+500, dataValue[2]*2.0+260, -dataValue[1]+1200);
	}

	public Point3D rangeConvert(Vector LeapCoor) {
		return rangeConvert(new float[] { LeapCoor.getX(), LeapCoor.getY(), LeapCoor.getZ() });
	}

	public Point3D rangeConvert(float[] LeapValue) {
		return new Point3D (LeapValue[0]*2+500, LeapValue[2]*2+260, -LeapValue[1]*4+1700);
	}

	public SubScene getSubScene() { return subScene; }
}

