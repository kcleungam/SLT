
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

import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.HandList;
import com.leapmotion.leap.Vector;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class Visualizer extends Applet {

	private SimpleUniverse u;
	private BranchGroup scene;
	private TransformGroup objGroup = new TransformGroup();

	// finger tips
	private Sphere fingertip = new Sphere(0.05f);
	// palms
	private Sphere palm = new Sphere(0.15f);

	private static float[] appStart = { -1.0f, -1.0f, 0.0f };
	private static float[] appEnd = { 1.0f, 1.0f, 2.0f };
	private static float[] leapStart = { -173.5f, 82.5f, -173.5f };
	private static float[] leapEnd = { 173.5f, 417.5f, 173.5f };

	public Visualizer() {
		setLayout(new BorderLayout());
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		Canvas3D c = new Canvas3D(config);
		add("Center", c);
		// Create a simple scene...
		scene = createSceneGraph();

		// and attach it to the virtual universe
		u = new SimpleUniverse(c);
		u.getViewingPlatform().setNominalViewingTransform();
		u.addBranchGraph(scene);
	}

	public BranchGroup createSceneGraph() {
		// Create the root of the branch graph
		BranchGroup objRoot = new BranchGroup();
		objGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		objRoot.addChild(objGroup);

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
	 * add spheres
	 */
	public void addSphere(Vector coordinate, Sphere type) {
		float[] graphvec = new float[] { coordinate.getX(), coordinate.getY(), coordinate.getZ() };
		graphvec = rangeConvert(graphvec);
		Transform3D pos1 = new Transform3D();
		pos1.setTranslation(new Vector3f(graphvec));
		objGroup.setTransform(pos1);
		objGroup.addChild(type);
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
		for (Hand hand : hands) {
			for (Finger finger : hand.fingers()) {
				addSphere(finger.tipPosition(), fingertip);
			}
			addSphere(hand.palmPosition(), palm);
		}
	}

	public float[] rangeConvert(float[] LeapValue) {
		float[] appValue = new float[3];
		for (int i = 0; i < 3; i++) {
			appValue[i] = (LeapValue[i] - leapStart[i]) * 2.0f / (leapEnd[i] - leapStart[i]) + appStart[i];
			if (appValue[i] > appEnd[i]) {
				appValue[i] = appEnd[i];
			}
		}
		return appValue;
	}

}
