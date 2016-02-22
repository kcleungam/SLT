import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Created by Krauser on 5/1/2016.
 */
public class Coordinate {
    @JsonProperty("x")
    public double x;
    @JsonProperty("y")
    public double y;
    @JsonProperty("z")
    public double z;


    public Coordinate(){

    }
    @JsonCreator
    public Coordinate(@JsonProperty("x") double x,@JsonProperty("y") double y,@JsonProperty("z") double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    @JsonSetter("x")
    public void setX(double x) {
        this.x = x;
    }
    @JsonSetter("y")
    public void setY(double y) {
        this.y = y;
    }
    @JsonSetter("z")
    public void setZ(double z) {
        this.z = z;
    }
    @JsonGetter("x")
    public double getX() {
        return x;
    }
    @JsonGetter("y")
    public double getY() {
        return y;
    }
    @JsonGetter("z")
    public double getZ() {
        return z;
    }
}
