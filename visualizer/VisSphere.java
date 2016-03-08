/**
 * This class adds several functions that should be used in this program.
 */
package visualizer;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.geometry.Point3D;

/**
 * @author Jacky
 *
 */
public class VisSphere extends Sphere {

	public VisSphere() {
		super();
		// Creating PhongMaterial
		PhongMaterial material = new PhongMaterial();
		// Diffuse Color
		material.setDiffuseColor(Color.ORANGE);
		// Specular Color
		material.setSpecularColor(Color.BLACK);
		setMaterial(material);
	}

	public VisSphere(double d) {
		super(d);
		// Creating PhongMaterial
		PhongMaterial material = new PhongMaterial();
		// Diffuse Color
		material.setDiffuseColor(Color.ORANGE);
		// Specular Color
		material.setSpecularColor(Color.BLACK);
		setMaterial(material);
		this.setTranslate(0, 0, -100);
	}

	public VisSphere(Point3D coordinate, double d) {
		super(d);
		this.setTranslate(coordinate);
	}

	public void setTranslate(double xvalue, double yvalue, double zvalue) {
		this.setTranslateX(xvalue);
		this.setTranslateY(yvalue);
		this.setTranslateZ(zvalue);
	}

	public void setTranslate(double[] value) {
		this.setTranslateX(value[0]);
		this.setTranslateY(value[1]);
		this.setTranslateZ(value[2]);
	}

	public void setTranslate(Point3D coordinate) {
		this.setTranslateX(coordinate.getX());
		this.setTranslateY(coordinate.getY());
		this.setTranslateZ(coordinate.getZ());
	}
}
