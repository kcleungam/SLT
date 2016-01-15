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

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.HandList;
import com.leapmotion.leap.Vector;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class Visualizer extends Applet implements Runnable {
	
	private SimpleUniverse u;
	private BranchGroup scene;
	private TransformGroup objGroup = new TransformGroup();
	private Canvas3D c;
	
	private Controller lmcontrol;
	
	// finger tips, follows the LM setting
	// i.e, 0 = left, 1 = right
	// 0 = thumb , etc
	private TransformGroup[][] fingers = new TransformGroup[2][5];
	
	// palms
	private TransformGroup[] palms = new TransformGroup[2] ;


	public Visualizer() {
	    setLayout(new BorderLayout());
	    GraphicsConfiguration config = SimpleUniverse
	        .getPreferredConfiguration();
	    Canvas3D c = new Canvas3D(config);
	    add("Center", c);
	    // Create a simple scene...
	    scene = createSceneGraph();
	    scene.addChild(objGroup);
	    
	    for (int i = 0; i < 2; i ++){
	    	for(int j = 0; j < 5; j ++){
	    		fingers[i][j] = new TransformGroup();
	    		fingers[i][j].addChild(new Sphere(0.1f));
	    	}
	    	palms[i] = new TransformGroup();
	    	palms[i].addChild(new Sphere(0.2f));
	    }
	    
	    lmcontrol = new Controller();
	    // and attach it to the virtual universe
	    u = new SimpleUniverse(c);
	    u.getViewingPlatform().setNominalViewingTransform();
	    u.addBranchGraph(scene);
	}
	
	public Visualizer(Controller controller) {
	    setLayout(new BorderLayout());
	    GraphicsConfiguration config = SimpleUniverse
	        .getPreferredConfiguration();
	    Canvas3D c = new Canvas3D(config);
	    add("Center", c);
	    // Create a simple scene
	    scene = createSceneGraph();
	    scene.addChild(objGroup);
	    
	    for (int i = 0; i < 2; i ++){
	    	for(int j = 0; j < 5; j ++){
	    		fingers[i][j] = new TransformGroup();
	    		fingers[i][j].addChild(new Sphere(0.1f));
	    		fingers[i][j].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	    	}
	    	palms[i] = new TransformGroup();
	    	palms[i].addChild(new Sphere(0.2f));
	    	palms[i].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	    }
	    
	    lmcontrol = controller;

	    // Attach the simple scene to the virtual universe
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
	public void updateSphere(Vector coordinate, TransformGroup translate){
		Sphere sphere = new Sphere(0.25f);
		Vector3f graphvec = new Vector3f(coordinate.getX(), coordinate.getY(), coordinate.getZ());
		
	    Transform3D pos1 = new Transform3D();
	    pos1.setTranslation(graphvec);
	    objGroup.setTransform(pos1);
	    if (objGroup.indexOfChild(translate) == -1){
	    	objGroup.addChild(sphere);
	    }
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

	public void traceLM(){
		while(true){
			Frame frame = lmcontrol.frame();
			HandList hands = frame.hands();
			if (hands.count() > 1) {
                for (int i = 0; i < 2 ; i ++){
                	for (int j = 0 ; j < 5 ; j ++){
                    	updateSphere(hands.get(i).fingers().get(j).tipPosition(), fingers[i][j]);
                	}
                	updateSphere(hands.get(i).palmPosition(), palms[i]);
                }
            } else if (hands.count() > 1){
            	if (hands.get(0).isLeft()) {
                	for (int j = 0 ; j < 5 ; j ++){
                    	updateSphere(hands.get(0).fingers().get(j).tipPosition(), fingers[0][j]);
                	}
                	updateSphere(hands.get(0).palmPosition(), palms[0]);
                } else{
                	for (int j = 0 ; j < 5 ; j ++){
                    	updateSphere(hands.get(1).fingers().get(j).tipPosition(), fingers[1][j]);
                	}
                	updateSphere(hands.get(0).palmPosition(), palms[1]);
                }
            } else {
            	clear();
            }
			
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			// may insert the play here...
		}
	}
	
	@Override
	public void run() {
		traceLM();
	}
}
