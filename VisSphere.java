/**
 * This class adds several functions that should be used in this program.
 */
package visualpack;

import javafx.scene.shape.Sphere;

/**
 * @author Jacky
 *
 */
public class VisSphere extends Sphere {

	public VisSphere() {
		super();
	}

	public VisSphere(double d) {
		super(d);
		this.setTranslate(0, 0, -100);
	}

	public VisSphere(VisCoordinate coordinate, double d) {
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

	public void setTranslate(VisCoordinate coordinate) {
		this.setTranslateX(coordinate.getX());
		this.setTranslateY(coordinate.getY());
		this.setTranslateZ(coordinate.getZ());
	}
}
