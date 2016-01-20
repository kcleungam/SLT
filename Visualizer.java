
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
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

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
	private SimpleUniverse u;
	private BranchGroup scene;
	private TransformGroup transscene;

	private final float[] appStart = { -1.0f, -1.0f, -1.0f };
	private final float[] appEnd = { 1.0f, 1.0f, 1.0f };
	private final float[] leapStart = { -300.0f, 0.0f, -300.0f };
	private final float[] leapEnd = { 300.0f, 600.0f, 300.0f };

	private TransformGroup[][] fingers = new TransformGroup[2][5];
	private TransformGroup[] palms = new TransformGroup[2];

	public Visualizer() {
		setLayout(new BorderLayout());
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		Canvas3D c = new Canvas3D(config);
		add(BorderLayout.CENTER, c);

		// Create a simple scene...
		scene = createSceneGraph();
		transscene = new TransformGroup();

		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 5; j++) {
				fingers[i][j] = new TransformGroup();
				fingers[i][j].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
				fingers[i][j].addChild(new Sphere(0.05f));
				//scene.addChild(fingers[i][j]);
			}
			palms[i] = new TransformGroup();
			palms[i].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
			palms[i].addChild(new Sphere(0.07f));
			//scene.addChild(palms[i]);
		}

		
		//transscene.addChild(new Sphere(0.15f));
		//scene.addChild(transscene);

		// and attach it to the virtual universe
		u = new SimpleUniverse(c);
		u.getViewingPlatform().setNominalViewingTransform();
		u.addBranchGraph(scene);
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
	 * add spheres
	 */
	public void addSphere(Vector coordinate, String type) {
		float[] graphvec = new float[] { coordinate.getX(), coordinate.getY(), coordinate.getZ() };
		double[] appCoor = rangeConvert(graphvec);
		Transform3D pos1 = new Transform3D();
		pos1.setTranslation(new Vector3d(appCoor));
		TransformGroup transgp = new TransformGroup(pos1);
		if (type == "fingertip") {
			transgp.addChild(new Sphere(0.05f));
		} else if (type == "palm") {
			transgp.addChild(new Sphere(0.07f));
		}
		transscene.addChild(transgp);
	}

	/*
	 * move spheres
	 */
	public void moveSphere(Vector coordinate, TransformGroup tg) {
		float[] graphvec = new float[] { coordinate.getX(), coordinate.getY(), coordinate.getZ() };
		double[] appCoor = rangeConvert(graphvec);
		Transform3D pos1 = new Transform3D();
		pos1.setTranslation(new Vector3d(appCoor));
		tg.setTransform(pos1);
	}

	public void moveSphere(TransformGroup tg) {
		Transform3D pos1 = new Transform3D();
		pos1.setTranslation(new Vector3d(0.0, 0.0, 4.0));// invisible
		tg.setTransform(pos1);
	}

	/*
	 * clear the spheres
	 */
	public void clear() {
		scene.detach();
		scene.removeAllChildren();
		u.addBranchGraph(scene);
	}

	public void traceLM1(Frame frame) {
		HandList hands = frame.hands();
		if (hands.count() == 2) {
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < 5; j++) {
					moveSphere(hands.get(i).finger(j).tipPosition(), fingers[i][j]);
				}
				moveSphere(hands.get(i).palmPosition(), palms[i]);
			}
		} else if (hands.count() == 1) {
			for (int j = 0; j < 5; j++) {
				moveSphere(hands.get(0).finger(j).tipPosition(), fingers[0][j]);
				//System.out.println(j);
			}
			moveSphere(hands.get(0).palmPosition(), palms[0]);
			for (int j = 0; j < 5; j++) {
				moveSphere(fingers[1][j]);
			}
			moveSphere(palms[1]);
		} else {
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < 5; j++) {
					moveSphere(fingers[i][j]);
				}
				moveSphere(palms[i]);
			}
		}
	}

	public void traceLM(Frame frame) { 
		// clear(); 
		scene.detach();
		scene.removeChild(transscene);
		transscene.removeAllChildren();
		HandList hands = frame.hands();
		for (Hand hand : hands) {
			for (Finger finger : hand.fingers()) {
				addSphere(finger.tipPosition(), "fingertip");
			}
			addSphere(hand.palmPosition(), "palm");
		}
		scene.addChild(transscene);
		u.addBranchGraph(scene);
	}

	public double[] rangeConvert(float[] LeapValue) {
		double[] appValue = new double[3];
		for (int i = 0; i < 3; i++) {
			appValue[i] = (LeapValue[i] - leapStart[i]) * 2.0f / (leapEnd[i] - leapStart[i]) + appStart[i];
			if (appValue[i] > appEnd[i]) {
				appValue[i] = appEnd[i];
			} else if (appValue[i] < appStart[i]) {
				appValue[i] = appStart[i];
			}
		}
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
				visualizer.traceLM1(controller.frame());
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
