import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GraphicsConfiguration;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Container;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Material;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.Timer;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.universe.SimpleUniverse;

/*
 * https://java3d.java.net/binary-builds.html
 * use the link above for the java 3d libraries,
 * vector math library included.
 * -Jacky
 */

public class Visualizer extends Applet{
	
	private SimpleUniverse u;
	private BranchGroup scene;
	
	private TransformGroup objTrans;
	
	private Canvas3D c;
		
	private double height = 0.0f;
	
	private double width = 0.0f;
	
	private Sphere lf1 = new Sphere(0.1f);
	private Sphere lf2 = new Sphere(0.1f);
	private Sphere lf3 = new Sphere(0.1f);
	private Sphere lf4 = new Sphere(0.1f);
	private Sphere lf5 = new Sphere(0.1f);
	private Sphere lp = new Sphere(0.2f);
	private Sphere rf1 = new Sphere(0.1f);
	private Sphere rf2 = new Sphere(0.1f);
	private Sphere rf3 = new Sphere(0.1f);
	private Sphere rf4 = new Sphere(0.1f);
	private Sphere rf5 = new Sphere(0.1f);
	private Sphere rp = new Sphere(0.2f);


	public Visualizer() {
	    setLayout(new BorderLayout());
	    GraphicsConfiguration config = SimpleUniverse
	        .getPreferredConfiguration();
	    Canvas3D c = new Canvas3D(config);
	    add("Center", c);
	    // Create a simple scene and attach it to the virtual universe
	    scene = createSceneGraph();

	    u = new SimpleUniverse(c);
	    u.getViewingPlatform().setNominalViewingTransform();
	    u.addBranchGraph(scene);
	}
	
	public BranchGroup createSceneGraph() {
		// Create the root of the branch graph
	    BranchGroup objRoot = new BranchGroup();
	    objTrans = new TransformGroup();
	    objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	    objRoot.addChild(objTrans);

	    // Create a simple shape leaf node, add it to the scene graph.
	    Vector3f vec = new Vector3f(0.0f, 0.0f, 0.0f);
	    objRoot.addChild(updateFingerTip(vec));
	    BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0),
	        100.0);

	    // Set up light source
	    Color3f light1Color = new Color3f(1.0f, 0.0f, 0.2f);
	    Vector3f light1Direction = new Vector3f(4.0f, -7.0f, -12.0f);
	    DirectionalLight light1 = new DirectionalLight(light1Color,
	        light1Direction);
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
	 * if has the sphere, add it/ translate it
	 * if no , delete it
	 */
	public TransformGroup updateFingerTip(Vector3f coordinate){
		Sphere sphere = new Sphere(0.25f);
		/*
		Appearance ap = new Appearance();	// in Java3D, u must set the color through Appearance 
		Material material = new Material();	// use material for 3d look
		
		if (hand == "left"){
			material = new Material(new Color3f(Color.red), new Color3f(Color.black), new Color3f(Color.red), new Color3f(Color.white), 70f);
		}else if(hand == "right"){
			material = new Material(new Color3f(Color.cyan), new Color3f(Color.black), new Color3f(Color.cyan), new Color3f(Color.white), 70f);
		}else{
			return null;
		}
		ap.setMaterial(material);
		sphere.setAppearance(ap);
		*/
		objTrans = new TransformGroup();
	    objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	    Transform3D pos1 = new Transform3D();
	    pos1.setTranslation(coordinate);
	    objTrans.setTransform(pos1);
	    objTrans.addChild(sphere);
		return objTrans;
	}
	
	/*
	 * clear the spheres
	 */
	public void clear(){
		u = new SimpleUniverse(c);
		u.addBranchGraph(createSceneGraph());
	}
	
	public static void main(String[] args) {
		Visualizer bb = new Visualizer();
	    MainFrame mf = new MainFrame(bb, 256, 256);
	}
}
