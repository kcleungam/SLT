/**
 * This coordinate is for visual pack internal use only, 
 * differ from the database one
 */
//package visualpack;

/**
 * @author Jacky
 *
 */
public class VisCoordinate {
	private double x;
	private double y;
	private double z;

	public VisCoordinate() {
		setX(0);
		setY(0);
		setZ(0);
	}

	public VisCoordinate(double[] coor) {
		this.x = coor[0];
		this.y = coor[1];
		this.z = coor[2];
	}

	public VisCoordinate(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public void setInfinity() {
		this.setX(Double.POSITIVE_INFINITY);
		this.setY(Double.POSITIVE_INFINITY);
		this.setZ(Double.POSITIVE_INFINITY);
	}

	public boolean checkInfinity() {
		return (this.getX() == Double.POSITIVE_INFINITY || this.getY() == Double.POSITIVE_INFINITY
				|| this.getZ() == Double.POSITIVE_INFINITY);
	}

}
