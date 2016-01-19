
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
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import com.leapmotion.leap.Frame;
import com.leapmotion.leap.HandList;
import com.leapmotion.leap.Vector;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class Visualizer extends Applet {

	private SimpleUniverse u;
	private BranchGroup scene;
	private TransformGroup objGroup = new TransformGroup();
	private Canvas3D c;

	// finger tips, follows the LM setting
	// i.e, 0 = left, 1 = right
	// 0 = thumb , etc
	private TransformGroup[][] fingers = new TransformGroup[2][5];

	// palms
	private TransformGroup[] palms = new TransformGroup[2];

	private static float appStart = -1.0f;
	private static float appEnd = 1.0f;
	private static float[] leapStart = { -173.5f, 82.5f, -173.5f };
	private static float[] leapEnd = { 173.5f, 417.5f, 173.5f };

	public Visualizer() {
		setLayout(new BorderLayout());
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		Canvas3D c = new Canvas3D(config);
		add("Center", c);
		// Create a simple scene...
		scene = createSceneGraph();
		scene.addChild(objGroup);
	    Transform3D pos1 = new Transform3D();
	    pos1.setTranslation(new Vector3f(0.0f, 0.0f, 0.0f));
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 5; j++) {
				fingers[i][j] = new TransformGroup();
				fingers[i][j].setTransform(pos1);
				fingers[i][j].addChild(new Sphere(0.1f));
			}
			palms[i] = new TransformGroup();
			palms[i].setTransform(pos1);
			palms[i].addChild(new Sphere(0.2f));
		}

		// and attach it to the virtual universe
		u = new SimpleUniverse(c);
		u.getViewingPlatform().setNominalViewingTransform();
		u.addBranchGraph(scene);
	}

	public BranchGroup createSceneGraph() {
		// Create the root of the branch graph
		BranchGroup objRoot = new BranchGroup();
		TransformGroup objTrans = new TransformGroup();
		objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		objRoot.addChild(objTrans);

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
	 * if has the sphere, add it/ translate it if no , delete it
	 */
	public void updateSphere(Vector coordinate, TransformGroup translate) {
		Sphere sphere = new Sphere(0.25f);
		float[] graphvec = new float[] { coordinate.getX(), coordinate.getY(), coordinate.getZ() };
		graphvec = rangeConvert(graphvec);
		Transform3D pos1 = new Transform3D();
		pos1.setTranslation(new Vector3f(graphvec));
		objGroup.setTransform(pos1);
		objGroup.addChild(translate);
	}

	/*
	 * clear the spheres
	 */
	public void clear() {
		objGroup.removeAllChildren();
	}

	public static void main(String[] args) {
		Visualizer bb = new Visualizer();
		MainFrame mf = new MainFrame(bb, 256, 256);
	}

	public void traceLM(Frame frame) {
		clear();
		HandList hands = frame.hands();
		if (hands.count() > 1) {
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < 5; j++) {
					updateSphere(hands.get(i).fingers().get(j).tipPosition(), fingers[i][j]);
				}
				updateSphere(hands.get(i).palmPosition(), palms[i]);
			}
		} else if (hands.count() > 1) {
			if (hands.get(0).isLeft()) {
				for (int j = 0; j < 5; j++) {
					updateSphere(hands.get(0).fingers().get(j).tipPosition(), fingers[0][j]);
				}
				updateSphere(hands.get(0).palmPosition(), palms[0]);
			} else {
				for (int j = 0; j < 5; j++) {
					updateSphere(hands.get(0).fingers().get(j).tipPosition(), fingers[1][j]);
				}
				updateSphere(hands.get(0).palmPosition(), palms[1]);
			}
		}
	}

	public float[] rangeConvert(float[] LeapValue) {
		float[] appValue = new float[3];
		for (int i = 0; i < 3; i++) {
			appValue[i] = (LeapValue[i] - leapStart[i])*2.0f / (leapEnd[i] - leapStart[i]) + appStart;
			if (appValue[i] > appEnd) {
				appValue[i] = appEnd;
			}
		}
		return appValue;
	}

}
