
/*
 * https://java3d.java.net/binary-builds.html
 * use the link above for the java 3d libraries,
 * vector math library included.
 * - Jacky
 */

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineStripArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.leapmotion.leap.Bone;
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.HandList;
import com.leapmotion.leap.Vector;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class Visualizer extends Applet {
	private final float[] appStart = { -1.5f, -1.5f, -1.5f };
	private final float[] appEnd = { 1.5f, 1.5f, 1.5f };
	private final float[] leapStart = { -200.0f, 0.0f, -200.0f };
	private final float[] leapEnd = { 200.0f, 400.0f, 200.0f };

	private final Vector3d INVINSIBLE_COOR = new Vector3d(0.0, 0.0, 14.0);

	private final int[] stripVertexCounts = { 5, 5, 5, 5, 5, 5, 5, 5, 5, 5 };

	private SimpleUniverse u;
	private BranchGroup scene;

	private TransformGroup[][][] fingertg = new TransformGroup[2][5][4];
	private TransformGroup[] palmstg = new TransformGroup[2];

	private LineStripArray geometry = new LineStripArray(50, GeometryArray.COORDINATES, stripVertexCounts);

	private Vector3d[][][] fingerCoor = new Vector3d[2][5][4];
	private Vector3d[] palmCoor = new Vector3d[2];

	public Visualizer() {
		setLayout(new BorderLayout());
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		Canvas3D c = new Canvas3D(config);
		add(BorderLayout.CENTER, c);

		// Create a simple scene...
		scene = createSceneGraph();
		// initialize the hand
		initializeParam();

		// and attach it to the virtual universe
		u = new SimpleUniverse(c);
		u.getViewingPlatform().setNominalViewingTransform();
		u.addBranchGraph(scene);
	}

	public void initializeParam() {
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 5; j++) {
				for (int k = 0; k < 4; k++) {
					fingerCoor[i][j][k] = INVINSIBLE_COOR;
					fingertg[i][j][k] = new TransformGroup();
					fingertg[i][j][k].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
					fingertg[i][j][k].addChild(new Sphere(0.03f));
					scene.addChild(fingertg[i][j][k]);
				}
			}
			palmCoor[i] = INVINSIBLE_COOR;
			palmstg[i] = new TransformGroup();
			palmstg[i].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
			palmstg[i].addChild(new Sphere(0.07f));
			scene.addChild(palmstg[i]);
		}
		geometry.setCapability(LineStripArray.ALLOW_COORDINATE_WRITE);
		geometry.setCoordinates(0, getPointArray());
		Shape3D lineShape = new Shape3D(geometry);
		scene.addChild(lineShape);

	}

	public BranchGroup createSceneGraph() {
		// Create the root of the branch graph
		BranchGroup objRoot = new BranchGroup();

		objRoot.setCapability(BranchGroup.ALLOW_DETACH);
		objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);

		// Create a simple shape leaf node, add it to the scene graph.
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);

		// Set up light source
		Color3f light1Color = new Color3f(1.0f, 0.0f, 0.2f);
		Vector3f light1Direction = new Vector3f(4.0f, -7.0f, -12.0f);
		DirectionalLight light1 = new DirectionalLight(light1Color, light1Direction);
		light1.setInfluencingBounds(bounds);
		objRoot.addChild(light1);

		// Set up the ambient light
		Color3f ambientColor = new Color3f(1.0f, 1.0f, 1.0f);
		AmbientLight ambientLightNode = new AmbientLight(ambientColor);
		ambientLightNode.setInfluencingBounds(bounds);
		objRoot.addChild(ambientLightNode);

		return objRoot;
	}

	/*
	 * add spheres by coordinates
	 */
	public void addSphere(Vector3d coordinate, float size) {
		Transform3D pos1 = new Transform3D();
		pos1.setTranslation(new Vector3d(coordinate));
		TransformGroup transgp = new TransformGroup(pos1);
		transgp.addChild(new Sphere(size));
		scene.addChild(transgp);
	}

	/*
	 * add spheres by coordinates
	 */
	public void addSphere(Vector3f coordinate, float size) {
		Vector3d transcoor = new Vector3d(coordinate);
		addSphere(transcoor, size);
	}

	/*
	 * move spheres to somewhere invisible
	 */
	public void moveSphere(TransformGroup tg) {
		Transform3D pos1 = new Transform3D();
		pos1.setTranslation(new Vector3d(0.0, 0.0, 14.0));// invisible
		tg.setTransform(pos1);
	}

	/*
	 * clear the screen
	 */
	public void clear() {
		scene.detach();
		scene.removeAllChildren();
		u.addBranchGraph(scene);
	}

	public void traceLM(Frame frame) {
		setCoor(frame);
		updateGraphic();
	}

	public Vector3d[][][] getFingerCoor() {
		return fingerCoor;
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
				fingerCoor[i][j][0] = new Vector3d(rangeConvert(hands.get(i).fingers().get(j).tipPosition()));
				fingerCoor[i][j][1] = new Vector3d(
						rangeConvert(hands.get(i).fingers().get(j).bone(Bone.Type.TYPE_DISTAL).prevJoint()));
				fingerCoor[i][j][2] = new Vector3d(
						rangeConvert(hands.get(i).fingers().get(j).bone(Bone.Type.TYPE_INTERMEDIATE).prevJoint()));
				fingerCoor[i][j][3] = new Vector3d(
						rangeConvert(hands.get(i).fingers().get(j).bone(Bone.Type.TYPE_PROXIMAL).prevJoint()));
			}
			palmCoor[i] = new Vector3d(rangeConvert(hands.get(i).palmPosition()));
		}
		// for non-existing hands:
		for (; 0 <= i && i < 2; i++) {
			for (int j = 0; j < 5; j++) {
				for (int k = 0; k < 4; k++) {
					fingerCoor[i][j][k] = INVINSIBLE_COOR;
				}
			}
			palmCoor[i] = INVINSIBLE_COOR;
		}
	}

	public void updateGraphic() {
		// update spheres
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 5; j++) {
				for (int k = 0; k < 4; k++) {
					Transform3D pos1 = new Transform3D();
					pos1.setTranslation(new Vector3d(fingerCoor[i][j][k]));
					fingertg[i][j][k].setTransform(pos1);
				}
			}
			Transform3D pos1 = new Transform3D();
			pos1.setTranslation(new Vector3d(palmCoor[i]));
			palmstg[i].setTransform(pos1);
		}
		// update lines
		geometry.setCoordinates(0, getPointArray());
	}

	public Point3d[] getPointArray() {
		Point3d[] output = new Point3d[50];
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 5; j++) {
				for (int k = 0; k < 4; k++) {
					output[i * 5 * 5 + j * 5 + k] = new Point3d(fingerCoor[i][j][k]);
				}
				output[i * 5 * 5 + (j + 1) * 5 - 1] = new Point3d(palmCoor[i]);
			}
		}
		return output;
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
		double[] appValue = { temp[0], -temp[2], -temp[1] };

		return appValue;
	}

	public static void main(String[] args) {
		SampleListener sampleListener = new SampleListener();
		Controller controller = new Controller();
		controller.addListener(sampleListener);
		Visualizer visualizer = new Visualizer();
		MainFrame mf = new MainFrame(visualizer, 512, 512);
		while (true) {
			try {
				visualizer.traceLM(controller.frame());
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
