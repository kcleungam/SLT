package visualizer;

import java.util.ArrayList;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.geometry.Point3D;

/**
*	http://netzwerg.ch/blog/2015/03/22/javafx-3d-line/
* @author Rahel Luthy
*/
public class PolyCylinder3D {
	private ArrayList<Cylinder> line;
	private int radius;
	private Color color;

	public PolyCylinder3D() {
		line = new ArrayList<Cylinder>();
	}

	public PolyCylinder3D(ArrayList<Point3D> points, int radius, Color color) {
		line = new ArrayList<Cylinder>();
		this.radius = radius;
		this.color = color;
		int length = points.size();
		for (int i = 0; i < length - 1; i++) {
		line.add(createConnection(points.get(i), points.get(i + 1)));
		}
	}

	public void update(ArrayList<Point3D> points) {
		int length = points.size();
		for (int i = 0; i < length - 1; i++) {
			updateConnection(line.get(i), points.get(i), points.get(i + 1));
		}
	}

	public void updateConnection(Cylinder cylinder, Point3D origin, Point3D target) {
		Point3D yAxis = new Point3D(0, 1, 0);
		Point3D diff = target.subtract(origin);
		Point3D mid = target.midpoint(origin);
		Translate moveToMidpoint = new Translate(mid.getX(), mid.getY(), mid.getZ());

		Point3D axisOfRotation = diff.crossProduct(yAxis);
		double angle = Math.acos(diff.normalize().dotProduct(yAxis));
		Rotate rotateAroundCenter = new Rotate(-Math.toDegrees(angle), axisOfRotation);

		cylinder.getTransforms().addAll(moveToMidpoint, rotateAroundCenter);
	}
	
	public Cylinder createConnection(Point3D origin, Point3D target) {
		Point3D yAxis = new Point3D(0, 1, 0);
		Point3D diff = target.subtract(origin);
		double height = diff.magnitude();

		Point3D mid = target.midpoint(origin);
		Translate moveToMidpoint = new Translate(mid.getX(), mid.getY(), mid.getZ());

		Point3D axisOfRotation = diff.crossProduct(yAxis);
		double angle = Math.acos(diff.normalize().dotProduct(yAxis));
		Rotate rotateAroundCenter = new Rotate(-Math.toDegrees(angle), axisOfRotation);

		Cylinder line = new Cylinder(radius, height);
//		PhongMaterial material = new PhongMaterial(color);
//        material.setDiffuseColor(color);
//        material.setSpecularColor(color);
		line.setMaterial(new PhongMaterial(color));

		line.getTransforms().addAll(moveToMidpoint, rotateAroundCenter);

		return line;
	}

	public ArrayList<Cylinder> getLine() {
		return line;
	}

	public void setLine(ArrayList<Cylinder> line) {
		this.line = line;
	}
}
